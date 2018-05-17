package com.intellif.eyecloud.presenter;
import com.intellif.eyecloud.bean.ImageBean;
import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.AddBKModel;
import com.intellif.eyecloud.view.IAddBkContact;
import java.io.File;
import java.util.List;
/**
 * Created by intellif on 2017/9/14.
 */
public class AddBKPersenter implements IAddBkContact.IAddBKPresenter {
    private IAddBkContact.IAddBKView addBKView;
    private IAddBkContact.IAddBKModel addBKModel;
    public  AddBKPersenter(IAddBkContact.IAddBKView addBKView){
        this.addBKView = addBKView;
        addBKModel = new AddBKModel();
    }
    @Override
    public void uploadImage(File file) {
        addBKView.showProgress();
        addBKModel.uploadImage(file, new OnHttpCallBack<ImageBean>() {
            @Override
            public void onSuccessful(ImageBean imageBean) {
                if(imageBean.faces<=0){
                    addBKView.showDialog("当前照片没有人脸");
                }else if(imageBean.faces>1){
                    addBKView.showDialog("当前照片包含多个人脸，请重新选取");
                }else if(imageBean.faceList.size()==1){
                    addBKView.setImage(imageBean);
                }
                addBKView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                addBKView.showInfo(errorMsg);
                addBKView.hideProgress();
            }
        });
    }
    @Override
    public void addInfo() {
        addBKView.showProgress();
        List<String> images= addBKView.getImageId();
        String areaId = addBKView.getArea();
        String PersonName = addBKView.getPersonName();
        String PersonDes = addBKView.getPersonDes();
        if(images.size()==0){
            addBKView.showInfo("请上传布控照片");
            addBKView.hideProgress();
            return;
        }
        if(PersonName.isEmpty()){
            addBKView.showInfo("布控名称不能为空");
            addBKView.hideProgress();
            return;
        }
        if(PersonDes.isEmpty()){
            addBKView.showInfo("附加信息描述不能为空");
            addBKView.hideProgress();
            return;
        }
        if(PersonName.length()>8){
            addBKView.showInfo("布控名称不能超过8个字符");
            addBKView.hideProgress();
            return;
        }
        if(PersonDes.length()>8){
            addBKView.showInfo("附加信息描述不能超过8个字符");
            addBKView.hideProgress();
            return;
        }
        String imgs="";
            if(images.size()==1){
                imgs=images.get(0);
            }
            else if(images.size()==2){
                imgs = images.get(0)+","+images.get(1);
            }
            else if(images.size()==3){
                imgs = images.get(0)+","+images.get(1)+","+images.get(2);
            }
        addBKModel.upload(imgs, areaId, PersonName, PersonDes, new OnHttpCallBack<PeopleBean>() {
            @Override
            public void onSuccessful(PeopleBean peopleBean) {
                addBKView.hideProgress();
                addBKView.activityFinish();
            }
            @Override
            public void onFaild(String errorMsg) {
                addBKView.hideProgress();
                addBKView.showInfo(errorMsg);
            }
        });
    }
}
