package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.BaseRepository;
import org.wf.dp.dniprorada.model.Merchant;

public interface MerchantDao extends BaseRepository<Merchant> {

   Merchant findBySID(String sID);

   boolean deleteBySID(String sID);

}