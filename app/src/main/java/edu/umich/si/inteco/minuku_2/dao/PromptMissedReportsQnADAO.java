package edu.umich.si.inteco.minuku_2.dao;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.config.UserPreferences;
import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku_2.model.PromptMissedReportsQnADataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by shriti on 11/9/16.
 */

public class PromptMissedReportsQnADAO implements DAO<PromptMissedReportsQnADataRecord> {

    private String TAG = "PromptMissedReportsQnADAO";
    private String myUserEmail;
    private UUID uuID;

    public PromptMissedReportsQnADAO() {
        myUserEmail = UserPreferences.getInstance().getPreference(Constants.KEY_ENCODED_EMAIL);

    }
    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(PromptMissedReportsQnADataRecord entity) throws DAOException {
        Log.d(TAG, "Adding missed report prompt question answer record.");
        Firebase dataRecordListRef = new Firebase(Constants.FIREBASE_URL_MISSED_REPORT_PROMPT_QNA)
                .child(myUserEmail)
                .child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        dataRecordListRef.push().setValue(entity);
    }

    @Override
    public void delete(PromptMissedReportsQnADataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<PromptMissedReportsQnADataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<PromptMissedReportsQnADataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(PromptMissedReportsQnADataRecord oldEntity, PromptMissedReportsQnADataRecord newEntity) throws DAOException {

    }
}
