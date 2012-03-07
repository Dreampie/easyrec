/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.model.core;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is a VO (valueobject/dataholder) for an easyrec database <code>Action</code>.
 * All typed attributes use a different set of integer ids for each type.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $<br/>
 * $Revision: 140 $</p>
 *
 * @author Roman Cerny
 */
public class ActionVO<I extends Comparable<I>, T extends Comparable<T>>
        implements Cloneable, Serializable {
    /////////////////////////////////////////////////////////////////////////
    // constants
    private static final long serialVersionUID = -2041480024266710365L;

    // HINT: add a possibility to import actions from a .CSV file with a given date (Mantis Issue: #582)
    /**
     * the number of columns a .CSV file must contain (tenantId, userId, sessionId, ip, itemId, itemTypeId, actionTypeId, ratingValue, searchSucceeded, numberOfFoundItems, description)
     * Note: the attributes id and actionTime are left out, since they are automatically generated by the database
     */
    public static final int CSV_NUMBER_OF_COLUMNS = 11;
    /////////////////////////////////////////////////////////////////////////
    // members
    private Integer id;
    private I tenant;
    private I user;
    private String sessionId;
    private String ip;
    private ItemVO<I,T> item;
    private T actionType;
    private Integer ratingValue;
    private Boolean searchSucceeded;
    private Integer numberOfFoundItems;
    private String description;
    private Date actionTime;

    //////////////////////////////////////////////////////////////////////////////
    // public methods

    // constructor

    /**
     * Constructor used for the import of new actions (actionTime and id will be generated)
     *
     * @param tenant             tenant the action belonged to
     * @param user               the user that performed the action
     * @param sessionId          a sessionId
     * @param ip                 ip address of the remote host performing the action
     * @param item               item on which the action was performed
     * @param actionType         the type of the action
     * @param ratingValue        depending on the actionType a value may be bound to the action
     * @param searchSucceeded    feedback field for search/lookup actions; indicates that items were found
     * @param numberOfFoundItems feedback field for search/lookup actions; indicates the number of items found
     * @param description        a description for the action
     */
    public ActionVO(I tenant, I user, String sessionId, String ip, ItemVO<I,T> item, T actionType,
                    Integer ratingValue, Boolean searchSucceeded, Integer numberOfFoundItems, String description) {
        this(null, tenant, user, sessionId, ip, item, actionType, ratingValue, searchSucceeded, numberOfFoundItems,
                description, null);
    }

    /**
     * Constructor used for the import of third party actions (that already contain an actionTime)     *
     *
     * @param tenant             tenant the action belonged to
     * @param user               the user that performed the action
     * @param sessionId          a sessionId
     * @param ip                 ip address of the remote host performing the action
     * @param item               item on which the action was performed
     * @param actionType         the type of the action
     * @param ratingValue        depending on the actionType a value may be bound to the action
     * @param searchSucceeded    feedback field for search/lookup actions; indicates that items were found
     * @param numberOfFoundItems feedback field for search/lookup actions; indicates the number of items found
     * @param description        a description for the action
     * @param actionTime         time the action was performed
     */
    public ActionVO(I tenant, I user, String sessionId, String ip, ItemVO<I,T> item, T actionType,
                    Integer ratingValue, Boolean searchSucceeded, Integer numberOfFoundItems, String description,
                    Date actionTime) {
        this(null, tenant, user, sessionId, ip, item, actionType, ratingValue, searchSucceeded, numberOfFoundItems,
                description, actionTime);
    }

    /**
     * Constructor used for queried actions from the database
     *
     * @param id                 a unique identifier for the action
     * @param tenant             tenant the action belonged to
     * @param user               the user that performed the action
     * @param sessionId          a sessionId
     * @param ip                 ip address of the remote host performing the action
     * @param item               item on which the action was performed
     * @param actionType         the type of the action
     * @param ratingValue        depending on the actionType a value may be bound to the action
     * @param searchSucceeded    feedback field for search/lookup actions; indicates that items were found
     * @param numberOfFoundItems feedback field for search/lookup actions; indicates the number of items found
     * @param description        a description for the action
     * @param actionTime         time the action was performed
     */
    public ActionVO(Integer id, I tenant, I user, String sessionId, String ip, ItemVO<I,T> item, T actionType,
                    Integer ratingValue, Boolean searchSucceeded, Integer numberOfFoundItems, String description,
                    Date actionTime) {
        this.id = id;
        this.tenant = tenant;
        this.user = user;
        this.sessionId = sessionId;
        this.ip = ip;
        this.item = item;
        this.actionType = actionType;
        this.ratingValue = ratingValue;
        this.searchSucceeded = searchSucceeded;
        this.numberOfFoundItems = numberOfFoundItems;
        this.description = description;
        this.actionTime = actionTime;
    }

    // getter/setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public I getTenant() {
        return tenant;
    }

    public void setTenant(I tenant) {
        this.tenant = tenant;
    }

    public I getUser() {
        return user;
    }

    public void setUser(I user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public T getActionType() {
        return actionType;
    }

    public void setActionType(T actionType) {
        this.actionType = actionType;
    }

    public ItemVO<I,T> getItem() {
        return item;
    }

    public void setItem(ItemVO<I,T> item) {
        this.item = item;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Boolean getSearchSucceeded() {
        return searchSucceeded;
    }

    public void setSearchSucceeded(Boolean searchSucceeded) {
        this.searchSucceeded = searchSucceeded;
    }

    public Integer getNumberOfFoundItems() {
        return numberOfFoundItems;
    }

    public void setNumberOfFoundItems(Integer numberOfFoundItems) {
        this.numberOfFoundItems = numberOfFoundItems;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getSimpleName());
        s.append('@');
        s.append(Integer.toHexString(hashCode()));
        s.append("[id=");
        s.append(id);
        s.append(",tenant=");
        s.append(tenant);
        s.append(",user=");
        s.append(user);
        s.append(",sessionId=");
        s.append(sessionId);
        s.append(",ip=");
        s.append(ip);
        s.append(", item=");
        s.append(item);
        s.append(", actionType=");
        s.append(actionType);
        s.append(", ratingValue=");
        s.append(ratingValue);
        s.append(", searchSucceeded=");
        s.append(searchSucceeded);
        s.append(", numberOfFoundItems=");
        s.append(numberOfFoundItems);
        s.append(", description=");
        s.append(description);
        s.append(", actionTime=");
        s.append(actionTime);
        s.append("]");
        return s.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actionTime == null) ? 0 : actionTime.hashCode());
        result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((numberOfFoundItems == null) ? 0 : numberOfFoundItems.hashCode());
        result = prime * result + ((ratingValue == null) ? 0 : ratingValue.hashCode());
        result = prime * result + ((searchSucceeded == null) ? 0 : searchSucceeded.hashCode());
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    /**
     * Implemetation of the <code>Cloneable</code> interface
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ActionVO<I,T> other = (ActionVO<I,T>) obj;
        if (actionTime == null) {
            if (other.actionTime != null) return false;
        } else if (!actionTime.equals(other.actionTime)) return false;
        if (actionType == null) {
            if (other.actionType != null) return false;
        } else if (!actionType.equals(other.actionType)) return false;
        if (description == null) {
            if (other.description != null) return false;
        } else if (!description.equals(other.description)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (ip == null) {
            if (other.ip != null) return false;
        } else if (!ip.equals(other.ip)) return false;
        if (item == null) {
            if (other.item != null) return false;
        } else if (!item.equals(other.item)) return false;
        if (numberOfFoundItems == null) {
            if (other.numberOfFoundItems != null) return false;
        } else if (!numberOfFoundItems.equals(other.numberOfFoundItems)) return false;
        if (ratingValue == null) {
            if (other.ratingValue != null) return false;
        } else if (!ratingValue.equals(other.ratingValue)) return false;
        if (searchSucceeded == null) {
            if (other.searchSucceeded != null) return false;
        } else if (!searchSucceeded.equals(other.searchSucceeded)) return false;
        if (sessionId == null) {
            if (other.sessionId != null) return false;
        } else if (!sessionId.equals(other.sessionId)) return false;
        if (tenant == null) {
            if (other.tenant != null) return false;
        } else if (!tenant.equals(other.tenant)) return false;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
    }

    /**
     * Checks whether the <code>Action</code> was performed on ("belongs to") the given item
     *
     * @param item the item to be compared to the item connected to the <code>Action</code>
     * @return <code>true</code> if the <code>Action</code> points to the given item, <code>false</code> else
     */
    public boolean isBelongingToItem(ItemVO<I,T> item) {
        if (this.item == null) {
            if (item != null) {
                return false;
            }
        } else {
            return this.item.equals(item);
        }
        return true;
    }

    /**
     * Checks whether two <code>Action</code> objects point to the same item
     *
     * @param action
     * @return
     */
    public boolean hasSameBelongingItem(ActionVO<I,T> action) {
        return isBelongingToItem(action.getItem());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ActionVO<I,T> clone() throws CloneNotSupportedException {
        ActionVO<I,T> clonedActionVO = (ActionVO<I,T>) super.clone();
        clonedActionVO.setItem(clonedActionVO.getItem().clone());
        return clonedActionVO;
    }
}
