package org.igov.service.business.action.task.listener.doc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("GetDocument_UkrDoc")
public class GetDocument_UkrDoc extends AbstractModelTask implements TaskListener {

    private final static Logger LOG = LoggerFactory.getLogger(GetDocument_UkrDoc.class);

    private Expression sID_Document;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask dt) {
        DelegateExecution execution = dt.getExecution();
        String sID_Document = getStringFromFieldExpression(this.sID_Document, execution);

        LOG.info("Parameters of the task sID_Document:{}", sID_Document);

        String sessionId = UkrDocUtil.getSessionId(generalConfig.getSID_login(), generalConfig.getSID_password(),
                generalConfig.sURL_AuthSID_PB() + "?lang=UA");

        String[] documentIDs = sID_Document.split(":");
        if (documentIDs.length > 1) { //почему больше одного?? не ошибка ли это?
            String url = String.format("/%s/%s/content", documentIDs[1], documentIDs[0]);

            LOG.info("Retrieved session ID:{} and created URL to request: {}", sessionId, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);

            String resp = new RestRequest().get(generalConfig.getsUkrDocServerAddress() + url, MediaType.APPLICATION_JSON, StandardCharsets.UTF_8, String.class, headers);

            LOG.info("Ukrdoc response getDocument:" + resp);
            org.activiti.engine.impl.util.json.JSONObject respJson = new org.activiti.engine.impl.util.json.JSONObject(resp);
            Object content = respJson.get("content");

            if (content != null) {

                String name = (String) ((org.activiti.engine.impl.util.json.JSONObject) content).get("name");
                runtimeService.setVariable(execution.getProcessInstanceId(), "sHead_Document", name);
                String text = (String) ((org.activiti.engine.impl.util.json.JSONObject) content).get("text");
                runtimeService.setVariable(execution.getProcessInstanceId(), "sBody_Document", text);
                try {
                    List<Map<String, Object>> files = (List<Map<String, Object>>) ((Map) ((org.activiti.engine.impl.util.json.JSONObject) content).get("extensions")).get("files");

                    //получение контента файла и прикрипление его в качестве атача к таске
                    if (files != null && !files.isEmpty()) {
                        StringBuilder anID_Attach_UkrDoc = new StringBuilder();
                        for (Map<String, Object> file : files) {
                            String view_url = (String) file.get("view_url"); ///docs/2016/10300131/files/10300000/content?type=.jpg
                            String[] part_URI = view_url.split("/");
                            if (part_URI.length == 6) {
                                String fileType = part_URI[5].substring(part_URI[5].indexOf("."));
                                String fileName = part_URI[4] + fileType;
                                LOG.info("view_url:" + view_url + " fileName: " + fileName);
                                resp = new RestRequest().get(generalConfig.getsUkrDocServerAddress() + view_url, MediaType.APPLICATION_JSON, StandardCharsets.UTF_8, String.class, headers);
                                LOG.info("Ukrdoc response getContentFile:" + resp);
                                try {
                                    ByteArrayMultipartFile oByteArrayMultipartFile = new ByteArrayMultipartFile(resp.getBytes(), "Приложение", fileName, "content-type");
                                    Attachment attachment = createAttachment(oByteArrayMultipartFile, dt, "Приложение", "anID_Attach_UkrDoc"); //добавить номер
                                    if (attachment != null) {
                                        anID_Attach_UkrDoc.append(attachment.getId()).append(",");
                                    } //"file": "a10300000.jpg", "name": "Приложение", 
                                } catch (Exception ex) {
                                    java.util.logging.Logger.getLogger(GetDocument_UkrDoc.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                LOG.info("Ukrdoc response getContentFile:" + resp);
                            }
                        }
                        runtimeService.setVariable(execution.getProcessInstanceId(), "anID_Attach_UkrDoc", anID_Attach_UkrDoc.toString());
                    }
                } catch (Exception ex) {
                    LOG.error("error getFiles: ", ex);
                }

            }
        }
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
