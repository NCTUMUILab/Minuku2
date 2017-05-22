package edu.umich.si.inteco.minuku.dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import edu.umich.si.inteco.minuku.model.ActivityRecognitionDataRecord;
import edu.umich.si.inteco.minukucore.dao.DAO;
import edu.umich.si.inteco.minukucore.dao.DAOException;
import edu.umich.si.inteco.minukucore.user.User;

/**
 * Created by Lawrence on 2017/5/22.
 */

public class ActivityRecognitionDataRecordDAO implements DAO<ActivityRecognitionDataRecord> {

    File file;
    BufferedWriter fw;
    JSONObject obj;

    public ActivityRecognitionDataRecordDAO(){
        //file = new File(Context.getFilesDir(), filename);
        file = new File("/output.json"); //, false
        //fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), "UTF-8"));
        obj = new JSONObject();



    }

    @Override
    public void setDevice(User user, UUID uuid) {

    }

    @Override
    public void add(ActivityRecognitionDataRecord entity) throws DAOException {

        //TODO store in Json file.
        try {
            obj.put(String.valueOf(entity.getCreationTime()), entity.getMostProbableActivity().toString());

            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), "UTF-8"));

            fw.write(obj.toString());

            fw.newLine();

            fw.flush();


        }catch (JSONException e){

        }catch (IOException e){

        }finally {
            if (fw != null) {
                try {
                    fw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void delete(ActivityRecognitionDataRecord entity) throws DAOException {

    }

    @Override
    public Future<List<ActivityRecognitionDataRecord>> getAll() throws DAOException {
        return null;
    }

    @Override
    public Future<List<ActivityRecognitionDataRecord>> getLast(int N) throws DAOException {
        return null;
    }

    @Override
    public void update(ActivityRecognitionDataRecord oldEntity, ActivityRecognitionDataRecord newEntity) throws DAOException {

    }
}
