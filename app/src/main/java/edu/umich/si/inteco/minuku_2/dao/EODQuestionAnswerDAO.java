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
import edu.umich.si.inteco.minuku_2.model.EODQuestionDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by shriti on 10/12/16.
 */

public class EODQuestionAnswerDAO implements DAO<EODQuestionDataRecord> {

    private String TAG = "EODQuestionAnswerDAO";
    private String myUserEmail;
    private UUID uuID;

    public EODQuestionAnswerDAO() {
        myUserEmail = UserPreferences.getInstance().getPreference(Constants.KEY_ENCODED_EMAIL);

    }
    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(EODQuestionDataRecord entity) throws DAOException {
        Log.d(TAG, "Adding EOD question answer record.");
        Firebase dataRecordListRef = new Firebase(Constants.FIREBASE_URL_EOD_QUESTION_ANSWER)
                .child(myUserEmail)
                .child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        dataRecordListRef.push().setValue(entity);
    }

    @Override
    public void delete(EODQuestionDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<EODQuestionDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<EODQuestionDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(EODQuestionDataRecord oldEntity, EODQuestionDataRecord newEntity) throws DAOException {

    }
}

