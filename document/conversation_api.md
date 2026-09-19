# 通用对话模块接口文档

## 1. 获取对话详情

### 接口地址

`GET /api/v1/user/conversation/{conversationId}`

### 功能描述

获取指定对话的详细信息

### 请求参数

| 参数名         | 类型    | 位置   | 是否必填 | 描述                               |
| -------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization  | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| conversationId | Integer | Path   | 是       | 对话 ID                            |

### 返回参数

| 参数名                 | 类型    | 描述                           |
| ---------------------- | ------- | ------------------------------ |
| code                   | Integer | 响应状态码，成功为 200         |
| msg                    | String  | 响应消息                       |
| data                   | Object  | 对话详情数据                   |
| data.conversationId    | Integer | 对话 ID                        |
| data.type              | Integer | 对话类型                       |
| data.status            | Integer | 对话状态                       |
| data.lastMessageTime   | Date    | 最后消息时间                   |
| data.lastMessage       | String  | 最后消息内容                   |
| data.lastMessageSender | String  | 最后消息发送者                 |
| data.targetName        | String  | 目标名称（桩主名称或用户名称） |

### 枚举说明

- **对话类型(type)**：

  - 0：客服对话
  - 1：预约对话
  - 2：紧急呼叫对话
  - 3：V2V 对话
  - 4：普通用户聊天

- **对话状态(status)**：
  - 1：进行中
  - 2：已完成
  - 3：已关闭

### 响应示例

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "conversationId": 1,
    "type": 1,
    "status": 1,
    "lastMessageTime": "2023-10-01 14:30:00",
    "lastMessage": "您好，我想预约您的充电桩",
    "lastMessageSender": "张三",
    "targetName": "李四（桩主）"
  }
}
```

---

## 2. 查询用户对话列表

### 接口地址

`GET /api/v1/user/conversation/list`

### 功能描述

查询当前用户的所有对话列表

### 请求参数

| 参数名        | 类型    | 位置   | 是否必填 | 描述                               |
| ------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| pageNum       | Integer | Query  | 否       | 页码，默认 1                       |
| pageSize      | Integer | Query  | 否       | 每页条数，默认 20                  |

### 返回参数

| 参数名                        | 类型    | 描述                           |
| ----------------------------- | ------- | ------------------------------ |
| code                          | Integer | 响应状态码，成功为 200         |
| msg                           | String  | 响应消息                       |
| data                          | Object  | 分页结果数据                   |
| data.total                    | Long    | 总记录数                       |
| data.list                     | Array   | 对话列表                       |
| data.list[].conversationId    | Integer | 对话 ID                        |
| data.list[].type              | Integer | 对话类型                       |
| data.list[].status            | Integer | 对话状态                       |
| data.list[].lastMessageTime   | Date    | 最后消息时间                   |
| data.list[].lastMessage       | String  | 最后消息内容                   |
| data.list[].lastMessageSender | String  | 最后消息发送者                 |
| data.list[].targetName        | String  | 目标名称（桩主名称或用户名称） |
| data.pageNum                  | Integer | 当前页码                       |
| data.pageSize                 | Integer | 每页条数                       |
| data.pages                    | Integer | 总页数                         |

### 枚举说明

- **对话类型(type)**：

  - 0：客服对话
  - 1：预约对话
  - 2：紧急呼叫对话
  - 3：V2V 对话
  - 4：普通用户聊天

- **对话状态(status)**：
  - 1：进行中
  - 2：已完成
  - 3：已关闭

### 响应示例

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 2,
    "list": [
      {
        "conversationId": 1,
        "type": 1,
        "status": 1,
        "lastMessageTime": "2023-10-01 14:30:00",
        "lastMessage": "您好，我想预约您的充电桩",
        "lastMessageSender": "张三",
        "targetName": "李四（桩主）"
      },
      {
        "conversationId": 2,
        "type": 0,
        "status": 1,
        "lastMessageTime": "2023-10-01 15:00:00",
        "lastMessage": "请问如何使用优惠券？",
        "lastMessageSender": "张三",
        "targetName": "客服人员"
      }
    ],
    "pageNum": 1,
    "pageSize": 20,
    "pages": 1
  }
}
```

---

## 3. 获取对话消息列表

### 接口地址

`GET /api/v1/user/conversation/{conversationId}/message`

### 功能描述

获取指定对话的所有消息记录

### 请求参数

| 参数名         | 类型    | 位置   | 是否必填 | 描述                               |
| -------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization  | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| conversationId | Integer | Path   | 是       | 对话 ID                            |
| pageNum        | Integer | Query  | 否       | 页码，默认 1                       |
| pageSize       | Integer | Query  | 否       | 每页条数，默认 20                  |

### 返回参数

| 参数名                  | 类型    | 描述                   |
| ----------------------- | ------- | ---------------------- |
| code                    | Integer | 响应状态码，成功为 200 |
| msg                     | String  | 响应消息               |
| data                    | Object  | 分页结果数据           |
| data.total              | Long    | 总记录数               |
| data.list               | Array   | 消息列表               |
| data.list[].id          | Integer | 消息 ID                |
| data.list[].senderId    | Integer | 发送者 ID              |
| data.list[].senderType  | Integer | 发送者类型             |
| data.list[].messageType | Integer | 消息类型               |
| data.list[].content     | String  | 消息内容               |
| data.list[].extraInfo   | String  | 扩展信息（如图片 URL） |
| data.list[].sendTime    | Date    | 发送时间               |
| data.list[].isRead      | Integer | 是否已读               |
| data.pageNum            | Integer | 当前页码               |
| data.pageSize           | Integer | 每页条数               |
| data.pages              | Integer | 总页数                 |

### 枚举说明

- **发送者类型(senderType)**：

  - 0：普通用户
  - 1：管理员
  - 2：系统

- **消息类型(messageType)**：

  - 0：文本
  - 1：图片
  - 2：语音
  - 3：系统通知

- **是否已读(isRead)**：
  - 0：未读
  - 1：已读

### 响应示例

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 3,
    "list": [
      {
        "id": 1,
        "senderId": 1001,
        "senderType": 0,
        "messageType": 0,
        "content": "您好，我想预约您的充电桩",
        "extraInfo": null,
        "sendTime": "2023-10-01 14:30:00",
        "isRead": 1
      },
      {
        "id": 2,
        "senderId": 1002,
        "senderType": 0,
        "messageType": 0,
        "content": "您好，可以的，请问您想预约什么时间？",
        "extraInfo": null,
        "sendTime": "2023-10-01 14:35:00",
        "isRead": 1
      },
      {
        "id": 3,
        "senderId": 1001,
        "senderType": 0,
        "messageType": 0,
        "content": "明天下午3点可以吗？",
        "extraInfo": null,
        "sendTime": "2023-10-01 14:40:00",
        "isRead": 0
      }
    ],
    "pageNum": 1,
    "pageSize": 20,
    "pages": 1
  }
}
```

---

## 4. 发送消息

### 接口地址

`POST /api/v1/user/conversation/{conversationId}`

### 功能描述

向指定对话发送消息

### 请求参数

| 参数名         | 类型    | 位置   | 是否必填 | 描述                               |
| -------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization  | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| conversationId | Integer | Path   | 是       | 对话 ID                            |
| content        | String  | Body   | 是       | 消息内容                           |
| messageType    | Integer | Body   | 否       | 消息类型，默认 0                   |
| extraInfo      | String  | Body   | 否       | 扩展信息（如图片 URL）             |

### 请求体示例

```json
{
  "content": "明天下午3点可以吗？",
  "messageType": 0,
  "extraInfo": null
}
```

### 返回参数

| 参数名 | 类型    | 描述                   |
| ------ | ------- | ---------------------- |
| code   | Integer | 响应状态码，成功为 200 |
| msg    | String  | 响应消息               |
| data   | Object  | 发送结果数据           |

### 枚举说明

- **消息类型(messageType)**：
  - 0：文本
  - 1：图片
  - 2：语音
  - 3：系统通知

### 响应示例

```json
{
  "code": 200,
  "msg": "消息发送成功",
  "data": null
}
```

---

## 5. 结束对话

### 接口地址

`DELETE /api/v1/user/conversation/{conversationId}`

### 功能描述

结束指定对话

### 请求参数

| 参数名         | 类型    | 位置   | 是否必填 | 描述                               |
| -------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization  | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| conversationId | Integer | Path   | 是       | 对话 ID                            |

### 返回参数

| 参数名 | 类型    | 描述                   |
| ------ | ------- | ---------------------- |
| code   | Integer | 响应状态码，成功为 200 |
| msg    | String  | 响应消息               |
| data   | Object  | 操作结果数据           |

### 响应示例

```json
{
  "code": 200,
  "msg": "对话已结束",
  "data": null
}
```

---

## 6. 创建与私桩桩主预约对话

### 接口地址

`POST /api/v1/user/charging/private-station/{stationId}/conversation`

### 功能描述

创建与私桩桩主的预约对话

### 请求参数

| 参数名        | 类型    | 位置   | 是否必填 | 描述                               |
| ------------- | ------- | ------ | -------- | ---------------------------------- |
| Authorization | String  | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| stationId     | Integer | Path   | 是       | 私桩 ID                            |

### 返回参数

| 参数名              | 类型    | 描述                   |
| ------------------- | ------- | ---------------------- |
| code                | Integer | 响应状态码，成功为 200 |
| msg                 | String  | 响应消息               |
| data                | Object  | 对话创建结果           |
| data.conversationId | Integer | 对话 ID                |
| data.stationId      | Integer | 私桩 ID                |
| data.ownerId        | Integer | 桩主用户 ID            |
| data.ownerName      | String  | 桩主名称               |

### 响应示例

```json
{
  "code": 200,
  "msg": "对话创建成功",
  "data": {
    "conversationId": 1,
    "stationId": 1001,
    "ownerId": 1002,
    "ownerName": "李四"
  }
}
```

---

## 7. 发起人工客服咨询

### 接口地址

`POST /api/v1/user/service/manual/conversation`

### 功能描述

发起与人工客服的咨询对话

### 请求参数

| 参数名        | 类型   | 位置   | 是否必填 | 描述                               |
| ------------- | ------ | ------ | -------- | ---------------------------------- |
| Authorization | String | Header | 是       | JWT 认证令牌，格式：Bearer {token} |
| content       | String | Body   | 是       | 咨询内容                           |

### 请求体示例

```json
{
  "content": "请问如何使用优惠券？"
}
```

### 返回参数

| 参数名 | 类型    | 描述                   |
| ------ | ------- | ---------------------- |
| code   | Integer | 响应状态码，成功为 200 |
| msg    | String  | 响应消息               |
| data   | Object  | 操作结果数据           |

### 响应示例

```json
{
  "code": 200,
  "msg": "人工客服咨询已发起",
  "data": null
}
```

---

## 枚举类型汇总

### 对话类型(type)

| 枚举值 | 描述         |
| ------ | ------------ |
| 0      | 客服对话     |
| 1      | 预约对话     |
| 2      | 紧急呼叫对话 |
| 3      | V2V 对话     |
| 4      | 普通用户聊天 |

### 对话状态(status)

| 枚举值 | 描述   |
| ------ | ------ |
| 1      | 进行中 |
| 2      | 已完成 |
| 3      | 已关闭 |

### 发送者类型(senderType)

| 枚举值 | 描述     |
| ------ | -------- |
| 0      | 普通用户 |
| 1      | 管理员   |
| 2      | 系统     |

### 消息类型(messageType)

| 枚举值 | 描述     |
| ------ | -------- |
| 0      | 文本     |
| 1      | 图片     |
| 2      | 语音     |
| 3      | 系统通知 |

### 是否已读(isRead)

| 枚举值 | 描述 |
| ------ | ---- |
| 0      | 未读 |
| 1      | 已读 |
