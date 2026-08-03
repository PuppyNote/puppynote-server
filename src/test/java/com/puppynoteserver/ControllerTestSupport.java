package com.puppynoteserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.alertHistory.controller.alertHistory.AlertHistoryController;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryReadService;
import com.puppynoteserver.alertHistory.service.alertHistory.AlertHistoryService;
import com.puppynoteserver.alertSetting.controller.AlertSettingController;
import com.puppynoteserver.alertSetting.service.AlertSettingReadService;
import com.puppynoteserver.alertSetting.service.AlertSettingService;
import com.puppynoteserver.community.like.controller.PostLikeController;
import com.puppynoteserver.community.like.service.PostLikeService;
import com.puppynoteserver.community.post.controller.CommunityPostController;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.CommunityPostWriteService;
import com.puppynoteserver.home.controller.HomeController;
import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.pet.familyMembers.controller.FamilyMemberController;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberReadService;
import com.puppynoteserver.pet.familyMembers.service.FamilyMemberWriteService;
import com.puppynoteserver.pet.petItemPurchase.controller.PetItemPurchaseController;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseReadService;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseWriteService;
import com.puppynoteserver.pet.petItems.controller.PetItemController;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petItems.service.PetItemWriteService;
import com.puppynoteserver.pet.petWalkAlarms.controller.PetWalkAlarmController;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmWriteService;
import com.puppynoteserver.pet.pets.controller.PetController;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.PetWriteService;
import com.puppynoteserver.pet.walk.controller.WalkController;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.pet.walk.service.WalkWriteService;
import com.puppynoteserver.petTip.controller.PetTipController;
import com.puppynoteserver.petTip.service.PetTipReadService;
import com.puppynoteserver.storage.controller.StorageController;
import com.puppynoteserver.storage.service.S3StorageService;
import com.puppynoteserver.user.push.controller.DeviceTokenController;
import com.puppynoteserver.user.push.service.PushWriteService;
import com.puppynoteserver.user.userItemCategories.controller.UserItemCategoryController;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryReadService;
import com.puppynoteserver.user.userItemCategories.service.UserItemCategoryWriteService;
import com.puppynoteserver.user.users.controller.LoginController;
import com.puppynoteserver.user.users.controller.UserController;
import com.puppynoteserver.user.users.service.LoginService;
import com.puppynoteserver.user.users.service.UserReadService;
import com.puppynoteserver.user.users.service.UserService;
import com.puppynoteserver.global.logagent.LogAgentWebhookService;
import com.puppynoteserver.weather.controller.WeatherController;
import com.puppynoteserver.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        AlertHistoryController.class,
        AlertSettingController.class,
        PostLikeController.class,
        CommunityPostController.class,
        HomeController.class,
        FamilyMemberController.class,
        PetItemPurchaseController.class,
        PetItemController.class,
        PetWalkAlarmController.class,
        PetController.class,
        WalkController.class,
        PetTipController.class,
        StorageController.class,
        UserItemCategoryController.class,
        DeviceTokenController.class,
        LoginController.class,
        UserController.class,
        WeatherController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // global
    @MockitoBean
    protected LogAgentWebhookService logAgentWebhookService;

    // alertHistory
    @MockitoBean
    protected AlertHistoryService alertHistoryService;
    @MockitoBean
    protected AlertHistoryReadService alertHistoryReadService;

    // alertSetting
    @MockitoBean
    protected AlertSettingService alertSettingService;
    @MockitoBean
    protected AlertSettingReadService alertSettingReadService;

    // community
    @MockitoBean
    protected PostLikeService postLikeService;
    @MockitoBean
    protected CommunityPostReadService communityPostReadService;
    @MockitoBean
    protected CommunityPostWriteService communityPostWriteService;

    // home
    @MockitoBean
    protected HomeReadService homeReadService;

    // pet
    @MockitoBean
    protected FamilyMemberReadService familyMemberReadService;
    @MockitoBean
    protected FamilyMemberWriteService familyMemberWriteService;
    @MockitoBean
    protected PetItemPurchaseWriteService petItemPurchaseWriteService;
    @MockitoBean
    protected PetItemPurchaseReadService petItemPurchaseReadService;
    @MockitoBean
    protected PetItemWriteService petItemWriteService;
    @MockitoBean
    protected PetItemReadService petItemReadService;
    @MockitoBean
    protected PetWalkAlarmWriteService petWalkAlarmWriteService;
    @MockitoBean
    protected PetWalkAlarmReadService petWalkAlarmReadService;
    @MockitoBean
    protected PetReadService petReadService;
    @MockitoBean
    protected PetWriteService petWriteService;
    @MockitoBean
    protected WalkWriteService walkWriteService;
    @MockitoBean
    protected WalkReadService walkReadService;

    // petTip
    @MockitoBean
    protected PetTipReadService petTipReadService;

    // storage
    @MockitoBean
    protected S3StorageService s3StorageService;

    // user
    @MockitoBean
    protected UserItemCategoryWriteService userItemCategoryWriteService;
    @MockitoBean
    protected UserItemCategoryReadService userItemCategoryReadService;
    @MockitoBean
    protected PushWriteService pushWriteService;
    @MockitoBean
    protected LoginService loginService;
    @MockitoBean
    protected UserService userService;
    @MockitoBean
    protected UserReadService userReadService;

    // weather
    @MockitoBean
    protected WeatherService weatherService;
}
