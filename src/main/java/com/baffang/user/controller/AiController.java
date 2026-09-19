// package com.baffang.user.controller;

// import com.baffang.user.aiTools.CarAuthTools;
// import com.baffang.user.aiTools.DateTimeTools;
// import com.baffang.user.aiTools.GetUserInfoById;
// import com.baffang.user.aiTools.PrivateStationDetailTools;
// import com.baffang.user.aiTools.PrivateStationTools;
// import com.baffang.user.aiTools.PublicStationDetailTools;
// import com.baffang.user.aiTools.ManageStationOperateStatusTools;
// import com.baffang.user.aiTools.PrivateStationReserveTools;
// import com.baffang.user.aiTools.StationSearchTools;
// import com.baffang.user.aiTools.UpdatePrivateStationTools;
// import com.baffang.user.aiTools.UserInfoTools;
// import com.baffang.user.service.CarAuthService;
// import com.baffang.user.service.ConversationService;
// import com.baffang.user.service.PrivateStationService;
// import com.baffang.user.service.PrivateStationUseService;
// import com.baffang.user.service.PublicStationService;
// import com.baffang.user.service.StationSearchService;
// import com.baffang.user.service.UserService;

// import org.apache.ibatis.annotations.Param;
// import org.springframework.ai.tool.ToolCallback;
// import org.springframework.ai.tool.ToolCallbacks;
// import org.springframework.ai.tool.definition.ToolDefinition;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.baffang.common.entity.UserInfo;
// import com.baffang.common.util.UserAiUtil;

// import java.util.ArrayList;
// import java.util.List;

// @RestController
// @RequestMapping("/user/ai")
// public class AiController {
//     @Autowired
//     UserAiUtil userAiUtil;

//     @Autowired
//     UserService userService;

//     @Autowired
//     CarAuthService carAuthService;

//     @Autowired
//     PrivateStationService privateStationService;

//     @Autowired
//     PrivateStationUseService privateStationUseService;

//     @Autowired
//     ConversationService conversationService;

//     @Autowired
//     PublicStationService publicStationService;

//     @Autowired
//     StationSearchService stationSearchService;

 

//     @GetMapping("/sendMessage")
//     public String sendMessage(@Param("message") String message) {
//         List<ToolCallback> toolCallbacks = new ArrayList<>();
//        // addToolToList(toolCallbacks, dateTimeTools);
//         addToolToList(toolCallbacks, new UserInfoTools(userService));
//         addToolToList(toolCallbacks, new GetUserInfoById(userService));
//         addToolToList(toolCallbacks, new CarAuthTools(carAuthService));
//         addToolToList(toolCallbacks, new PrivateStationTools(privateStationService));
//         addToolToList(toolCallbacks, new PrivateStationDetailTools(privateStationService));
//         addToolToList(toolCallbacks, new UpdatePrivateStationTools(privateStationService));
//         addToolToList(toolCallbacks, new ManageStationOperateStatusTools(privateStationService));
//         addToolToList(toolCallbacks, new PublicStationDetailTools(publicStationService));
//         addToolToList(toolCallbacks, new PrivateStationReserveTools(privateStationUseService, conversationService));
//         addToolToList(toolCallbacks, new StationSearchTools(stationSearchService));
//         return userAiUtil.sendMessage("现在是系统测试", message, toolCallbacks);
//     }

//     public void addToolToList(List<ToolCallback> toolCallbacks, Object tool) {
//         ToolCallback[] toolCallbackArray = ToolCallbacks.from(tool);
//         for (ToolCallback toolCallback : toolCallbackArray) {
//             toolCallbacks.add(toolCallback);
//         }
//     }

// }
