package com.pptom.robot.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.pptom.robot.domain.WeChatMessage;
import com.pptom.robot.util.HttpClientUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tom.tang
 * @date 2018/7/18
 * @email tom.tang@sainstore.com
 * @description
 * @since 2018/7/18
 */
public class WeChatManager {
    /**
     * 创建一个WeChatManager对象
     */
    private static WeChatManager instance = new WeChatManager();

    /**
     * 让构造函数为 private，这样该类就不会被实例化
     */
    private WeChatManager() {
    }

    /**
     * 获取唯一可用的对象
     * @return
     */
    public static WeChatManager getInstance() {
        return instance;
    }

    private HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();
    /**
     * 是否在线
     */
    private boolean isAlive = false;
    /**
     *
     */
    private int memberCount = 0;

    private String indexUrl;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 昵称
     */
    private String nickName;

    private String uuid = null;

    private boolean useHotReload = false;

    private String hotReloadDir = "itchat.pkl";

    private int receivingRetryCount = 5;

    /**
     * 最后一次收到正常retcode的时间，秒为单位
     */
    private long lastNormalRetcodeTime;

    /**
     * 消息列表
     */
    private List<WeChatMessage> weChatMessageList = new ArrayList<>();

    /**
     * 登陆账号自身信息
     */
    private JsonNode userSelf;
    /**
     * 好友+群聊+公众号+特殊账号
     */
    private List<JSONObject> memberList = new ArrayList<>();
    /**
     * 好友
     */
    private List<JsonNode> contactList = new ArrayList<>();
    /**
     * 群
     */
    private List<JSONObject> groupList = new ArrayList<>();
    /**
     * 群聊成员字典
     */
    private Map<String, JSONArray> groupMemeberMap = new HashMap<>();
    /**
     * 公众号／服务号
     */
    private List<JSONObject> publicUsersList = new ArrayList<>();
    /**
     * 特殊账号
     */
    private List<JSONObject> specialUsersList = new ArrayList<>();
    /**
     * 群ID列表
     */
    private List<String> groupIdList = new ArrayList<>();
    /**
     * 群NickName列表
     */
    private List<String> groupNickNameList = new ArrayList<>();

    /**
     * 用户信息
     */
    private Map<String, JSONObject> userInfoMap = new HashMap<>();

    /**
     * 登录信息
     */
    private Map<String, Object> loginInfo = new HashMap<String, Object>();


    /**
     * 存放登录信息
     * @param key
     * @param object
     */
    public void putLoginInfo(String key, Object object) {
        this.loginInfo.put(key, object);
    }

    /**
     * 根据key取出登录信息
     * @param key
     * @return
     */
    public Object getFromLoginInfo(String key){
        return this.loginInfo.get(key);
    }

    /**
     * 请求参数
     */
    public Map<String, Object> getParamMap() {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> baseRequest = new HashMap<>();
        baseRequest.put("Uin", "wxuin");
        baseRequest.put("Sid", "wxsid");
        baseRequest.put("Skey", "skey");
        baseRequest.put("DeviceID", "pass_ticket");
        map.put("BaseRequest", baseRequest);
        return map;
    }

    /**
     * 添加群组id到list中
     * @param groupId
     * @return true 为不存在且添加到list，false为已存在且不操作
     */
    public boolean addGroupIdList(String groupId) {
        //如果不存在，则添加进去
        if (!this.groupIdList.contains(groupId)) {
            this.groupIdList.add(groupId);
            return true;
        }
        return false;
    }


    //==================================================================================================================

    public HttpClientUtil getHttpClientUtil() {
        return httpClientUtil;
    }

    public void setHttpClientUtil(HttpClientUtil httpClientUtil) {
        this.httpClientUtil = httpClientUtil;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isUseHotReload() {
        return useHotReload;
    }

    public void setUseHotReload(boolean useHotReload) {
        this.useHotReload = useHotReload;
    }

    public String getHotReloadDir() {
        return hotReloadDir;
    }

    public void setHotReloadDir(String hotReloadDir) {
        this.hotReloadDir = hotReloadDir;
    }

    public int getReceivingRetryCount() {
        return receivingRetryCount;
    }

    public void setReceivingRetryCount(int receivingRetryCount) {
        this.receivingRetryCount = receivingRetryCount;
    }

    public long getLastNormalRetcodeTime() {
        return lastNormalRetcodeTime;
    }

    public void setLastNormalRetcodeTime(long lastNormalRetcodeTime) {
        this.lastNormalRetcodeTime = lastNormalRetcodeTime;
    }

    public List<WeChatMessage> getWeChatMessageList() {
        return weChatMessageList;
    }

    public void setWeChatMessageList(List<WeChatMessage> weChatMessageList) {
        this.weChatMessageList = weChatMessageList;
    }


    public List<JSONObject> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<JSONObject> memberList) {
        this.memberList = memberList;
    }

    public List<JsonNode> getContactList() {
        return contactList;
    }

    public void setContactList(List<JsonNode> contactList) {
        this.contactList = contactList;
    }

    public List<JSONObject> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<JSONObject> groupList) {
        this.groupList = groupList;
    }

    public Map<String, JSONArray> getGroupMemeberMap() {
        return groupMemeberMap;
    }

    public void setGroupMemeberMap(Map<String, JSONArray> groupMemeberMap) {
        this.groupMemeberMap = groupMemeberMap;
    }

    public List<JSONObject> getPublicUsersList() {
        return publicUsersList;
    }

    public void setPublicUsersList(List<JSONObject> publicUsersList) {
        this.publicUsersList = publicUsersList;
    }

    public List<JSONObject> getSpecialUsersList() {
        return specialUsersList;
    }

    public void setSpecialUsersList(List<JSONObject> specialUsersList) {
        this.specialUsersList = specialUsersList;
    }

//    public List<String> getGroupIdList() {
//        return groupIdList;
//    }
//
//    public void setGroupIdList(List<String> groupIdList) {
//        this.groupIdList = groupIdList;
//    }

    public List<String> getGroupNickNameList() {
        return groupNickNameList;
    }

    public void setGroupNickNameList(List<String> groupNickNameList) {
        this.groupNickNameList = groupNickNameList;
    }

    public Map<String, JSONObject> getUserInfoMap() {
        return userInfoMap;
    }

    public void setUserInfoMap(Map<String, JSONObject> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    //==================================================================================================================


    public JsonNode getUserSelf() {
        return userSelf;
    }

    public void setUserSelf(JsonNode userSelf) {
        this.userSelf = userSelf;
    }

    public Map<String, Object> getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(Map<String, Object> loginInfo) {
        this.loginInfo = loginInfo;
    }
}
