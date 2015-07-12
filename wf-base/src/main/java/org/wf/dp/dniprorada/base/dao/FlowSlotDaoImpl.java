package org.wf.dp.dniprorada.base.dao;

import org.joda.time.DateTime;
import org.wf.dp.dniprorada.base.dao.util.QueryBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class FlowSlotDaoImpl extends AbstractCustomRepositoryImpl implements FlowSlotCustomDao {

   public int updateSlots(Long nID_Flow_ServiceData, Collection<DateTime> dates, String newDuration) {
      QueryBuilder qb = new QueryBuilder(getSession(), "update FlowSlot s set ");
      qb.append("s.sDuration = :DURATION ", newDuration);
      qb.append("where s.flow.id = :FLOW_ID and ", nID_Flow_ServiceData);
      qb.appendInSafe("s.sDate", "DATE", new ArrayList<>(dates));
      return qb.toQuery().executeUpdate();
   }
}
