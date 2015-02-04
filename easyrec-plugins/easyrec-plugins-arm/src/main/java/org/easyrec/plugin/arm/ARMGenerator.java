/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.easyrec.plugin.arm;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyrec.model.core.ItemAssocVO;
import org.easyrec.model.core.ItemVO;
import org.easyrec.plugin.arm.model.ARMConfiguration;
import org.easyrec.plugin.arm.model.ARMConfigurationInt;
import org.easyrec.plugin.arm.model.ARMStatistics;
import org.easyrec.plugin.arm.model.TupleVO;
import org.easyrec.plugin.arm.store.dao.RuleminingItemAssocDAO;
import org.easyrec.plugin.model.Version;
import org.easyrec.plugin.support.GeneratorPluginSupport;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import org.easyrec.plugin.generator.RunConditionEnabled;
import org.easyrec.utils.io.MySQL;

/**
 *
 * @author szavrel
 */
public class ARMGenerator extends GeneratorPluginSupport<ARMConfiguration, ARMStatistics> implements RunConditionEnabled {

    public static final String DISPLAY_NAME = "ARM";
    public static final Version VERSION = new Version("0.98");
    public static final URI ID = URI.create("http://www.easyrec.org/plugins/ARM");

    private static final Log logger = LogFactory.getLog(ARMGenerator.class);

    private AssocRuleMiningService assocRuleMiningService;
    private TupleCounter tupleCounter;
    private RuleminingItemAssocDAO ruleminingItemAssocDAO;
    //private TenantService tenantService;

    public ARMGenerator() {
        super(DISPLAY_NAME, ID, VERSION, ARMConfiguration.class, ARMStatistics.class);
    }

    @Override
    public ARMConfiguration newConfiguration() {
        return new ARMConfiguration();
    }

    @Override
    public boolean evaluateRuncondition(Date lastRun) {
        
        if (lastRun == null) return true; // never run before so execute anyway+
        ARMConfigurationInt intConfiguration;
        try {
            intConfiguration = assocRuleMiningService.mapTypesToConfiguration(getConfiguration());
        } catch (Exception ex) {
            logger.info("error in mapping configuration during evaluation of runcondition! Execution cancelled!");
            intConfiguration = null;
        }
        if (intConfiguration == null) return false; // if configuration cannot be mapped, no execution necessary
        int actionsSinceLastRun = assocRuleMiningService.getNumberOfActions(intConfiguration, lastRun);
        return actionsSinceLastRun >= 1;
    }

    @Override
    protected void doExecute(ExecutionControl control, ARMStatistics stats) throws Exception {

        control.updateProgress(1, 1, "Calculating # of baskets.");

        ARMConfiguration configuration = getConfiguration();
        ARMConfigurationInt intConfiguration;

        Date start = MySQL.sanitzeForMysql56(new Date());
        stats.setStartDate(start);

        try {
            intConfiguration = assocRuleMiningService.mapTypesToConfiguration(configuration);
            logger.info("TenantId:" + intConfiguration.getTenantId());
        } catch (Exception e) {
            stats.setException(e.getMessage());
            intConfiguration = null;
        }
        if (intConfiguration != null) {
            tupleCounter.init();
            if (control.isAbortRequested()) throw new Exception("ARM was manually aborted!");
            control.updateProgress(1, 6, "Calculating # of baskets.");
            Integer nrBaskets = assocRuleMiningService.getNumberOfBaskets(intConfiguration);
            stats.setNrBaskets(nrBaskets);

            if (control.isAbortRequested()) throw new Exception("ARM was manually aborted!");
            control.updateProgress(2, 6, "Calculating # of products.");
            Integer nrProducts = assocRuleMiningService.getNumberOfProducts(intConfiguration);
            stats.setNrProducts(nrProducts);

            Integer support = (int) (nrBaskets * (configuration.getSupportPrcnt() / 100));
            intConfiguration.setSupport(Math.max(support, configuration.getSupportMinAbs()));

            if (control.isAbortRequested()) throw new Exception("ARM was manually aborted!");
            control.updateProgress(3, 6, "Defining set L1.");
            TObjectIntHashMap<ItemVO<Integer, Integer>> L1 = assocRuleMiningService.defineL1(intConfiguration);
            stats.setSizeL1(L1.size());
            stats.setLastSupport(intConfiguration.getSupport());

            if (control.isAbortRequested()) throw new Exception("ARM was manually aborted!");
            control.updateProgress(4, 6, "Defining set L2.");
            List<TupleVO> L2 = assocRuleMiningService.defineL2(L1, tupleCounter, intConfiguration, stats);
            stats.setSizeL2(L2.size());

            if (control.isAbortRequested()) throw new Exception("ARM was manually aborted!");
            control.updateProgress(5, 6, "Generating rules.");

            if (configuration.getMaxRulesPerItem() == null) {
                List<ItemAssocVO<Integer,Integer>> rules = assocRuleMiningService.createRules(L2, L1,
                        intConfiguration, stats, configuration.getConfidencePrcnt());
                stats.setSizeRules(rules.size());
                for (ItemAssocVO<Integer,Integer> itemAssocVO : rules) {
                    //                try {
                    //                    ruleminingItemAssocDAO.insertItemAssoc(itemAssocVO);
                    //                } catch (DataIntegrityViolationException e) {
                    //                    ruleminingItemAssocDAO.updateItemAssocUsingUniqueKey(itemAssocVO);
                    //                }
                    ruleminingItemAssocDAO.insertOrUpdateItemAssoc(itemAssocVO);
                }
            } else {
                int count = 0;
                Collection<SortedSet<ItemAssocVO<Integer,Integer>>> rules = assocRuleMiningService.createBestRules(
                        L2, L1, intConfiguration, stats, configuration.getConfidencePrcnt());
                for (SortedSet<ItemAssocVO<Integer,Integer>> sortedSet : rules) {
                    count += sortedSet.size();
                    for (ItemAssocVO<Integer,Integer> itemAssocVO : sortedSet) {
                        //                   try {
                        //                        ruleminingItemAssocDAO.insertItemAssoc(itemAssocVO);
                        //                    } catch (DataIntegrityViolationException e) {
                        //                        ruleminingItemAssocDAO.updateItemAssocUsingUniqueKey(itemAssocVO);
                        //                    }
                        ruleminingItemAssocDAO.insertOrUpdateItemAssoc(itemAssocVO);
                    }
                }
                stats.setSizeRules(count);
                stats.setNumberOfRulesCreated(count);
            }
            stats.setLastConf(configuration.getConfidencePrcnt());
            stats.setNumberOfActionsConsidered(assocRuleMiningService.getNumberOfActions(intConfiguration, null));
                    // remove old Rules
            assocRuleMiningService.removeOldRules(intConfiguration, stats);
            //assocRuleMiningService.perform(configuration.getTenantId());

            control.updateProgress(6, 6, "Finished");
        } // TODO: else write logoutput 
        stats.setEndDate(MySQL.sanitzeForMysql56(new Date()));
        stats.setDuration((stats.getEndDate().getTime() - stats.getStartDate().getTime())/1000);
    }

    @Override
    public String getPluginDescription() {
        return "This plugin provides a simple algorithm for shopping basket analysis. "
                + "It generates rules of the type 'items that where frequently viewed/bought/good rated together.'";
    }


    // ----------------------------- GETTER / SETTER METHODS -------------------------------------

    public void setAssocRuleMiningService(AssocRuleMiningService assocRuleMiningService) {
        this.assocRuleMiningService = assocRuleMiningService;
    }

    public void setTupleCounter(TupleCounter tupleCounter) {
        this.tupleCounter = tupleCounter;
    }

    public void setRuleminingItemAssocDAO(RuleminingItemAssocDAO ruleminingItemAssocDAO) {
        this.ruleminingItemAssocDAO = ruleminingItemAssocDAO;
    }

}
