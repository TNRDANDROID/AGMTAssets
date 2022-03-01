package com.nic.AGMTAssets.Model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class RoadListValue {
public Integer roadCategoryCode;
public String roadCategory;
public Integer roadID;
public String roadCode;
public String roadName;
public String roadVillage;
private ItemType type;
private Integer totalAsset;
private Integer assetCapturedCount;
private Integer totalStartPoint;
private Integer totalMidPoint;
private Integer totalEndPoint;
private String state;

public  Integer locGroup;
public  Integer locID;
public  String groupName;
public  String subgroupName;
public String colLabel;
public String locationDetails;
public String levelType;
public String pointType;
public String roadLat;
public String roadLong;
public String assetId;
public Bitmap Image;

public Integer pmgsyDcode;
public Integer pmgsyBcode;
public Integer pmgsyPvcode;
public String pmgsyPvname;
public Integer pmgsyHabcode;
public String pmgsyHabName;
public String pmgsyHabNameTa;

public Integer dCode;
public Integer bCode;
public Integer pvCode;
public Integer habCode;
private String remark;
private String serverFlag;

private String dataType;
private Integer culvertType;
private String  culvertTypeName;
private Integer chainage;
private String culvertName;
private Integer span;
private Integer noOfSpan;
private Integer width;
private Integer ventHeight;
private Integer length;
private Integer culvertId;
private String startLat;
private String startLong;
private String imageAvailable;


private String form_id;
private String form_number;
private String form_name_ta;
private String no_of_photos;
private String type_of_photos;
private String min_no_of_photos;
private String max_no_of_photos;

    public String getMin_no_of_photos() {
        return min_no_of_photos;
    }

    public void setMin_no_of_photos(String min_no_of_photos) {
        this.min_no_of_photos = min_no_of_photos;
    }

    public String getMax_no_of_photos() {
        return max_no_of_photos;
    }

    public void setMax_no_of_photos(String max_no_of_photos) {
        this.max_no_of_photos = max_no_of_photos;
    }

    private String asseet_id;
private String state_code;
private String disp_id;
private String column_type;
private String disp_name;
private String disp_value;
private String text_value;
private String sl_no;
private String latitude;
private String longitude;
private String hab_asset_image;
private String flag;
private String photo_taken;

    public String getPhoto_taken() {
        return photo_taken;
    }

    public void setPhoto_taken(String photo_taken) {
        this.photo_taken = photo_taken;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getHab_asset_image() {
        return hab_asset_image;
    }

    public void setHab_asset_image(String hab_asset_image) {
        this.hab_asset_image = hab_asset_image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSl_no() {
        return sl_no;
    }

    public void setSl_no(String sl_no) {
        this.sl_no = sl_no;
    }

    public String getText_value() {
        return text_value;
    }

    public void setText_value(String text_value) {
        this.text_value = text_value;
    }

    public String getAsseet_id() {
        return asseet_id;
    }

    public void setAsseet_id(String asseet_id) {
        this.asseet_id = asseet_id;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getDisp_id() {
        return disp_id;
    }

    public void setDisp_id(String disp_id) {
        this.disp_id = disp_id;
    }

    public String getColumn_type() {
        return column_type;
    }

    public void setColumn_type(String column_type) {
        this.column_type = column_type;
    }

    public String getDisp_name() {
        return disp_name;
    }

    public void setDisp_name(String disp_name) {
        this.disp_name = disp_name;
    }

    public String getDisp_value() {
        return disp_value;
    }

    public void setDisp_value(String disp_value) {
        this.disp_value = disp_value;
    }

    public String getNo_of_photos() {
        return no_of_photos;
    }

    public void setNo_of_photos(String no_of_photos) {
        this.no_of_photos = no_of_photos;
    }

    public String getType_of_photos() {
        return type_of_photos;
    }

    public void setType_of_photos(String type_of_photos) {
        this.type_of_photos = type_of_photos;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getForm_number() {
        return form_number;
    }

    public void setForm_number(String form_number) {
        this.form_number = form_number;
    }

    public String getForm_name_ta() {
        return form_name_ta;
    }

    public void setForm_name_ta(String form_name_ta) {
        this.form_name_ta = form_name_ta;
    }

    public String getPmgsyHabNameTa() {
        return pmgsyHabNameTa;
    }

    public void setPmgsyHabNameTa(String pmgsyHabNameTa) {
        this.pmgsyHabNameTa = pmgsyHabNameTa;
    }

    public String getImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(String imageAvailable) {
        this.imageAvailable = imageAvailable;
    }

    public String getCulvertTypeName() {
        return culvertTypeName;
    }

    public void setCulvertTypeName(String culvertTypeName) {
        this.culvertTypeName = culvertTypeName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getCulvertType() {
        return culvertType;
    }

    public void setCulvertType(Integer culvertType) {
        this.culvertType = culvertType;
    }

    public Integer getChainage() {
        return chainage;
    }

    public void setChainage(Integer chainage) {
        this.chainage = chainage;
    }

    public String getCulvertName() {
        return culvertName;
    }

    public void setCulvertName(String culvertName) {
        this.culvertName = culvertName;
    }

    public Integer getSpan() {
        return span;
    }

    public void setSpan(Integer span) {
        this.span = span;
    }

    public Integer getNoOfSpan() {
        return noOfSpan;
    }

    public void setNoOfSpan(Integer noOfSpan) {
        this.noOfSpan = noOfSpan;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getVentHeight() {
        return ventHeight;
    }

    public void setVentHeight(Integer ventHeight) {
        this.ventHeight = ventHeight;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getCulvertId() {
        return culvertId;
    }

    public void setCulvertId(Integer culvertId) {
        this.culvertId = culvertId;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getServerFlag() {
        return serverFlag;
    }

    public void setServerFlag(String serverFlag) {
        this.serverFlag = serverFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String imageDescription;

    public Integer getPmgsyDcode() {
        return pmgsyDcode;
    }

    public void setPmgsyDcode(Integer pmgsyDcode) {
        this.pmgsyDcode = pmgsyDcode;
    }

    public Integer getPmgsyBcode() {
        return pmgsyBcode;
    }

    public void setPmgsyBcode(Integer pmgsyBcode) {
        this.pmgsyBcode = pmgsyBcode;
    }

    public Integer getPmgsyPvcode() {
        return pmgsyPvcode;
    }

    public void setPmgsyPvcode(Integer pmgsyPvcode) {
        this.pmgsyPvcode = pmgsyPvcode;
    }

    public String getPmgsyPvname() {
        return pmgsyPvname;
    }

    public void setPmgsyPvname(String pmgsyPvname) {
        this.pmgsyPvname = pmgsyPvname;
    }

    public Integer getPmgsyHabcode() {
        return pmgsyHabcode;
    }

    public void setPmgsyHabcode(Integer pmgsyHabcode) {
        this.pmgsyHabcode = pmgsyHabcode;
    }

    public String getPmgsyHabName() {
        return pmgsyHabName;
    }

    public void setPmgsyHabName(String pmgsyHabName) {
        this.pmgsyHabName = pmgsyHabName;
    }

    public Integer getdCode() {
        return dCode;
    }

    public void setdCode(Integer dCode) {
        this.dCode = dCode;
    }

    public Integer getbCode() {
        return bCode;
    }

    public void setbCode(Integer bCode) {
        this.bCode = bCode;
    }

    public Integer getPvCode() {
        return pvCode;
    }

    public void setPvCode(Integer pvCode) {
        this.pvCode = pvCode;
    }

    public Integer getHabCode() {
        return habCode;
    }

    public void setHabCode(Integer habCode) {
        this.habCode = habCode;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getRoadLat() {
        return roadLat;
    }

    public void setRoadLat(String roadLat) {
        this.roadLat = roadLat;
    }

    public String getRoadLong() {
        return roadLong;
    }

    public void setRoadLong(String roadLong) {
        this.roadLong = roadLong;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String createdDate;

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getColLabel() {
        return colLabel;
    }

    public void setColLabel(String colLabel) {
        this.colLabel = colLabel;
    }

    public Integer getLocGroup() {
        return locGroup;
    }

    public void setLocGroup(Integer locGroup) {
        this.locGroup = locGroup;
    }

    public Integer getLocID() {
        return locID;
    }

    public void setLocID(Integer locID) {
        this.locID = locID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSubgroupName() {
        return subgroupName;
    }

    public void setSubgroupName(String subgroupName) {
        this.subgroupName = subgroupName;
    }

    public enum ItemType {
        ONE_ITEM, TWO_ITEM;
    }
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    public String getRoadVillage() {
        return roadVillage;
    }

    public void setRoadVillage(String roadVillage) {
        this.roadVillage = roadVillage;
    }

    public Integer getRoadCategoryCode() {
        return roadCategoryCode;
    }

    public void setRoadCategoryCode(Integer roadCategoryCode) {
        this.roadCategoryCode = roadCategoryCode;
    }

    public String getRoadCategory() {
        return roadCategory;
    }

    public void setRoadCategory(String roadCategory) {
        this.roadCategory = roadCategory;
    }

    public Integer getRoadID() {
        return roadID;
    }

    public void setRoadID(Integer roadID) {
        this.roadID = roadID;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public Integer getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(Integer totalAsset) {
        this.totalAsset = totalAsset;
    }

    public Integer getAssetCapturedCount() {
        return assetCapturedCount;
    }

    public void setAssetCapturedCount(Integer assetCapturedCount) {
        this.assetCapturedCount = assetCapturedCount;
    }

    public Integer getTotalMidPoint() {
        return totalMidPoint;
    }

    public void setTotalMidPoint(Integer totalMidPoint) {
        this.totalMidPoint = totalMidPoint;
    }

    public Integer getTotalEndPoint() {
        return totalEndPoint;
    }

    public void setTotalEndPoint(Integer totalEndPoint) {
        this.totalEndPoint = totalEndPoint;
    }

    public Integer getTotalStartPoint() {
        return totalStartPoint;
    }

    public void setTotalStartPoint(Integer totalStartPoint) {
        this.totalStartPoint = totalStartPoint;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }
    public Integer getId() {
        return id;
    }
    private Integer id;
}