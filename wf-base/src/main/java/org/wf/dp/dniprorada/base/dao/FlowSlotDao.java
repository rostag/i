package org.wf.dp.dniprorada.base.dao;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.wf.dp.dniprorada.base.model.FlowSlot;

import java.util.List;
import java.util.Set;

public interface FlowSlotDao extends BaseRepository<FlowSlot>, FlowSlotCustomDao {

   /**
    * Gets flow slots by service data ID ordered by date in given interval
    * @param nID_ServiceData ID services data of slot flow.
    * @param startDate start date of interval (inclusive)
    * @param stopDate end date of interval (exclusive)
    * @return flow slots
    */
   @Query("select f from FlowSlot f where f.flow.nID_ServiceData = ?1 and (f.sDate >= ?2 and f.sDate  < ?3)")
   List<FlowSlot> findFlowSlotsByServiceData(Long nID_ServiceData, DateTime startDate, DateTime stopDate);

   /**
    *
    * @param nID_Flow_ServiceData ID of flow
    * @param startDate start date of interval (inclusive)
    * @param stopDate end date of interval (exclusive)
    * @return flow slots
    */
   @Query("select f from FlowSlot f where f.flow.id = ?1 and (f.sDate >= ?2 and f.sDate  < ?3)")
   List<FlowSlot> findFlowSlotsByFlow(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate);

   /**
    * Gets flow slots in given interval
    * @param nID_Flow_ServiceData ID of flow
    * @param startDate start date of interval (inclusive)
    * @param stopDate stop date of interval (inclusive)
    * @return set of date times
    */
   @Query("select f.sDate from FlowSlot f where f.flow.id = ?1 and f.sDate between ?2 and ?3")
   Set<DateTime> findFlowSlotsDates(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate);
}
