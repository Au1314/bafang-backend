# AI 聊天 WebSocket 接口文档

返回参数解释

```
/*
     * 来源
     * 1：来自Ai回复
     * 2：来自ToolCall返回
     */
    public Integer source;
    /**
     * ToolCall返回类型
     * 1: 调用声明（即ToolCall向前端返回消息指明Ai在调用该参数）
     * 2：直接返回信息（即ToolCall直接返回调用结果给用户，不经过Ai）
     * Ai回复时该字段为空
     */
    public Integer toolCallReturnType;
    /**
     * 调用的工具名称
     */
    public String toolName;
    /**
     * 常用于调用声明信息以及Ai直接返回的信息
     */
    public String message;
    /**
     * 常用于存储ToolCall直接返回信息
     */
    public Object data;
```

## 1. WebSocket 连接

### 连接地址

```
ws://localhost:8080/ws/ai-chat?token={用户token(不带Bearer)}
```

示例```
ws://localhost:8080/ws/ai-chat?token=eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjowLCJhdXRoVHlwZSI6InVzZXIiLCJ1c2VySWQiOjUsInVzZXJuYW1lIjoidGVzdF91c2VyIiwiaWF0IjoxNzY4Mjc1MTk5LCJleHAiOjE3Njg4Nzk5OTl9.T3kfc-C409Ngo5Erv2j3rpxpsmIuLDTekHPWWmIYl3o

````

### 连接建立流程

1. 客户端发起 WebSocket 连接请求
2. 服务器验证用户身份（从会话中获取 userId）
3. 连接成功后，服务器发送连接确认消息

## 2. 请求格式

### 消息类型

#### 2.1 聊天消息 (CHAT_MESSAGE)

```json
{
  "type": "CHAT_MESSAGE",
  "message": "你好，我想查询附近的充电站"
}
````

#### 2.2 心跳消息 (PING)

```json
{
  "type": "PING"
}
```

## 3. 返回形式

### 3.1 连接成功消息

```json
{
  "source": 2, // 来自系统
  "message": "AI聊天连接已建立，您可以开始提问了。"
}
```

### 3.2 AI 聊天回复

```json
{
  "source": 1, // 来自AI
  "toolCallReturnType": null, // AI直接回复时为空
  "message": "AI回复的内容"
}
```

注意：ai 回复为流式响应，每次回复 message 约 10~30token
示例

```
{"source":1,"toolCallReturnType":null,"toolName":null,"message":"我来帮您修改第一个充电","data":null}
{"source":1,"toolCallReturnType":null,"toolName":null,"message":"桩的信息。首先，我需要","data":null}
{"source":1,"toolCallReturnType":null,"toolName":null,"message":"查看您当前的充电桩列表","data":null}
{"source":1,"toolCallReturnType":null,"toolName":null,"message":"，然后获取第一个充电","data":null}
{"source":1,"toolCallReturnType":null,"toolName":null,"message":"桩的详细信息，以便进行","data":null}
```

### 3.3 错误消息

```json
{
  "source": 2, // 来自系统
  "message": "错误信息内容"
}
```

### 3.4 流结束消息

```json
{
  "source": 1, // 来自AI
  "message": "[STREAM_END]"
}
```

### 3.5 PONG 响应

```json
{
  "source": 2, // 来自系统
  "message": "pong"
}
```

## 4. AI 工具列表

| 工具名称                | 英文代码                        | 用途                        |
| ----------------------- | ------------------------------- | --------------------------- |
| 日期时间工具            | DateTimeTools                   | 处理日期时间相关操作        |
| 私人电站预约工具        | PrivateStationReserveTools      | 处理私人电站预约相关操作    |
| 电站搜索工具            | StationSearchTools              | 处理电站搜索相关操作        |
| 私人电站详情工具        | PrivateStationDetailTools       | 获取私人电站详细信息        |
| 私人电站工具            | PrivateStationTools             | 处理私人电站相关操作        |
| 公共电站详情工具        | PublicStationDetailTools        | 获取公共电站详细信息        |
| 更新私人电站工具        | UpdatePrivateStationTools       | 更新私人电站信息            |
| 车辆认证工具            | CarAuthTools                    | 处理车辆认证相关操作        |
| 车辆认证信息填写工具    | FillCarAuthInfoTools            | 帮助用户填写车辆认证信息    |
| 用户信息查询工具        | GetUserInfoById                 | 根据 ID 获取用户信息        |
| 电站运营状态管理工具    | ManageStationOperateStatusTools | 管理电站运营状态            |
| 用户信息 WebSocket 工具 | UserInfoWSTools                 | 通过 WebSocket 获取用户信息 |
| 充电桩信息工具          | StationInfoTools                | 向用户展示充电桩简略信息    |

## 5. 工具回调机制

所有 AI 工具都实现了 ToolCallback 接口，通过 ToolCallbacks.from()方法转换为 ToolCallback 数组，然后添加到工具列表中供 AI 调用。

## 6. 流式响应处理

服务器采用流式响应方式返回 AI 结果，使用 TokenBuffer 类进行令牌缓冲和拼接，优化用户体验：

- 最小令牌数：10
- 最大令牌数：30
- 当累积令牌数达到最小阈值时，发送部分响应
- 会话结束时发送剩余内容和结束标记

## 7. 错误处理

系统对以下情况进行错误处理：

- 未知消息类型
- JSON 解析异常
- AI 服务调用失败
- WebSocket 传输错误

所有错误都会通过错误消息格式返回给客户端。

## 8. 工具特殊说明

1. 更新私人电站工具（UpdatePrivateStationTools）在更新成功后会额外返回一条消息展示更新后的电站信息
   示例

```json
{
  "source": 2,
  "toolCallReturnType": 2,
  "toolName": "updatePrivateStation",
  "message": "",
  "data": {
    "id": 7,
    "name": "系统测试修改后的充电桩V2",
    "location": null,
    "locationName": "测试修改地点V2",
    "longitude": 116.482054,
    "latitude": 39.997746,
    "price": 2.5,
    "status": 0,
    "type": 1,
    "currentType": 1,
    "maxChargeVoltage": 240,
    "maxChargeCurrent": 100,
    "maxChargePower": 4000,
    "maxVehicles": 3,
    "currentVehicles": 0,
    "createTime": "2026-01-12 11:10:13",
    "updateTime": "2026-01-13 15:08:48",
    "openStatus": 1,
    "openTime": "2026-01-13 08:00:00",
    "closeTime": "2026-01-13 22:00:00",
    "ownerId": 5,
    "authStatus": 0,
    "image": "222222222222",
    "description": "这是系统测试修改后的充电桩描述V2，用于验证修改功能是否正常工作，这是第二次修改",
    "priceType": 1,
    "rating": 0.0,
    "dynamicRules": [
      {
        "ruleId": 9,
        "startTime": "08:00:00",
        "endTime": "18:00:00",
        "price": 1.5
      },
      {
        "ruleId": 10,
        "startTime": "18:00:00",
        "endTime": "22:00:00",
        "price": 2.0
      },
      {
        "ruleId": 11,
        "startTime": "22:00:00",
        "endTime": "08:00:00",
        "price": 1.2
      }
    ]
  }
}
```

2. 充电桩信息工具（StationInfoTools）会返回充电桩的简略信息，包括充电桩 ID、名称、地点名称、经纬度、价格、评分、距离、类型、开启状态、最大车辆数、当前车辆数、电流类型、最大充电电压、最大充电电流和最大充电功率
   示例

```json
{
  "source": 2,
  "toolCallReturnType": 2,
  "toolName": "getStationInfo",
  "message": "",
  "data": {
    "id": 1001,
    "name": "小区充电桩",
    "locationName": "北京市朝阳区建国路88号",
    "longitude": 116.482054,
    "latitude": 39.997746,
    "price": 1.8,
    "rating": 4.5,
    "distance": 500.5,
    "type": 2,
    "openStatus": 1,
    "maxVehicles": 5,
    "currentVehicles": 2,
    "currentType": 1,
    "maxChargeVoltage": 240,
    "maxChargeCurrent": 100,
    "maxChargePower": 4000
  }
}
```

3. 车辆认证信息填写工具（FillCarAuthInfoTools）会帮助用户填写车辆认证信息，将传入参数整理后返回前端，由用户在前端进行酌情修改后再提交认证
   示例

```json
{
  "source": 2,
  "toolCallReturnType": 2,
  "toolName": "fillCarAuthInfo",
  "message": "",
  "data": {
    "vehicleType": "电动汽车",
    "currentType": 1,
    "carBrand": "特斯拉",
    "carType": "Model 3",
    "maxChargeVoltage": 400,
    "maxChargeCurrent": 120,
    "maxChargePower": 48000,
    "batteryCapacity": 75.0,
    "dischargePower": 30000,
    "range": 550,
    "vehicleNumber": ""
  }
}
```
