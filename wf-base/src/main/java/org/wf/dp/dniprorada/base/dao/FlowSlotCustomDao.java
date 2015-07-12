package org.wf.dp.dniprorada.base.dao;

import org.joda.time.DateTime;

import java.util.Collection;

public interface FlowSlotCustomDao {
    /**
     * Updates slots with new duration
     * @param nID_Flow_ServiceData
     * @param dates
     * @param newDuration
     * @return count of slots updated
     */
    int updateSlots(Long nID_Flow_ServiceData, Collection<DateTime> dates, String newDuration);
}
