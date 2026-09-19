# AI对话接口文档

## 1. 接口概述

本模块提供了AI对话相关的接口，包括获取会话列表、获取会话历史记录和切换会话等功能。所有接口的统一前缀为 `/api/v1/user/`。

## 2. 接口列表

| 接口路径 | 方法 | 功能描述 |
|---------|------|----------|
| `/ai/conversations` | GET | 获取用户的AI对话会话列表 |
| `/ai/conversations/{sessionId}/messages` | GET | 获取指定会话的消息历史记录 |
| `/ai/conversations/{sessionId}/switch` | GET | 切换到指定会话 |

## 3. 接口详情

### 3.1 获取用户的AI对话会话列表

**接口路径**：`/api/v1/user/ai/conversations`

**请求方法**：GET

**功能描述**：获取当前用户的所有AI对话会话列表，按最后交互时间倒序排列。

**请求头**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| Authorization | string | 是 | Bearer + Token |

**请求示例**：

```http
GET /api/v1/user/ai/conversations
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "获取会话列表成功",
  "data": [
    {
      "conversationId": 1,
      "aiModel": "gpt-3.5-turbo",
      "sessionId": "session_123456789",
      "status": 0,
      "lastInteractionTime": "2026-01-12T15:30:45.000+08:00",
      "totalMessages": 5,
      "lastMessage": "这是最后一条消息的内容"
    },
    {
      "conversationId": 2,
      "aiModel": "gpt-3.5-turbo",
      "sessionId": "session_987654321",
      "status": 1,
      "lastInteractionTime": "2026-01-12T14:20:30.000+08:00",
      "totalMessages": 3,
      "lastMessage": "这是另一个会话的最后一条消息"
    }
  ]
}
```

### 3.2 获取指定会话的消息历史记录

**接口路径**：`/api/v1/user/ai/conversations/{sessionId}/messages`

**请求方法**：GET

**功能描述**：获取指定会话的所有消息历史记录，按发送时间正序排列。

**请求头**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| Authorization | string | 是 | Bearer + Token |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| sessionId | string | 是 | 会话ID |

**请求示例**：

```http
GET /api/v1/user/ai/conversations/session_123456789/messages
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "获取会话历史记录成功",
  "data": [
    {
      "messageId": 1,
      "senderType": 0,
      "messageType": 0,
      "content": "你好，我想了解一些关于充电桩的信息",
      "sendTime": "2026-01-12T15:25:10.000+08:00",
      "responseTime": null,
      "extraInfo": null
    },
    {
      "messageId": 2,
      "senderType": 1,
      "messageType": 0,
      "content": "当然可以，我很乐意为您解答关于充电桩的问题。请问您想了解哪方面的信息？",
      "sendTime": "2026-01-12T15:25:15.000+08:00",
      "responseTime": 5000,
      "extraInfo": null
    },
    {
      "messageId": 3,
      "senderType": 0,
      "messageType": 0,
      "content": "充电桩有哪些类型？",
      "sendTime": "2026-01-12T15:26:30.000+08:00",
      "responseTime": null,
      "extraInfo": null
    },
    {
      "messageId": 4,
      "senderType": 1,
      "messageType": 0,
      "content": "充电桩主要分为交流电充电桩和直流电充电桩两种类型...",
      "sendTime": "2026-01-12T15:26:35.000+08:00",
      "responseTime": 4800,
      "extraInfo": null
    },
    {
      "messageId": 5,
      "senderType": 0,
      "messageType": 0,
      "content": "非常感谢您的解答！",
      "sendTime": "2026-01-12T15:30:45.000+08:00",
      "responseTime": null,
      "extraInfo": null
    }
  ]
}
```

### 3.3 切换到指定会话

**接口路径**：`/api/v1/user/ai/conversations/{sessionId}/switch`

**请求方法**：GET

**功能描述**：切换到指定的会话，如果会话状态不是进行中，会将其更新为进行中。

**请求头**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| Authorization | string | 是 | Bearer + Token |

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| sessionId | string | 是 | 会话ID |

**请求示例**：

```http
GET /api/v1/user/ai/conversations/session_123456789/switch
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应示例**：

```json
{
  "code": 200,
  "msg": "切换会话成功",
  "data": null
}
```

## 4. 响应说明

所有接口的响应结构统一为：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| code | int | 状态码：200-成功，其他-失败 |
| msg | string | 响应消息 |
| data | object/array | 响应数据，根据接口不同返回对象或数组 |

### 4.1 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或授权失败 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 5. 数据字段说明

### 5.1 会话列表项（AIChatConversationListVO）

| 字段名 | 类型 | 说明 |
|-------|------|------|
| conversationId | Integer | 会话ID |
| aiModel | String | AI模型名称 |
| sessionId | String | 会话ID（用于WebSocket连接） |
| status | Integer | 会话状态：0-进行中，1-已结束，2-已中断 |
| lastInteractionTime | Date | 最后交互时间 |
| totalMessages | Integer | 消息总数 |
| lastMessage | String | 最后一条消息的内容 |

### 5.2 消息项（AIChatMessageVO）

| 字段名 | 类型 | 说明 |
|-------|------|------|
| messageId | Integer | 消息ID |
| senderType | Integer | 发送者类型：0-用户，1-AI |
| messageType | Integer | 消息类型：0-文本，1-系统通知，2-工具调用 |
| content | String | 消息内容 |
| sendTime | Date | 发送时间 |
| responseTime | Integer | 响应时间（毫秒），仅AI消息有值 |
| extraInfo | String | 额外信息，JSON格式 |