package com.pet.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.common.Result;
import com.pet.entity.ChatMessage;
import com.pet.mapper.ChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI智能对话（宠物咨询助手）
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Value("${ai.api-url}")
    private String aiApiUrl;

    @Value("${ai.api-key}")
    private String aiApiKey;

    @Value("${ai.model}")
    private String aiModel;

    /**
     * 发送消息给AI助手
     */
    @PostMapping
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> body,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String content = body.get("content");

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiModel);
        requestBody.put("messages", new Map[]{
                Map.of("role", "system", "content",
                        "你是一个专业的宠物救助领养助手，回答用户关于流浪动物救助、宠物领养、宠物护理、疫苗接种、绝育等问题。回答要亲切、专业、简洁。"),
                Map.of("role", "user", "content", content)
        });

        String reply;
        try {
            HttpResponse response = HttpRequest.post(aiApiUrl)
                    .header("Authorization", "Bearer " + aiApiKey)
                    .header("Content-Type", "application/json")
                    .body(cn.hutool.json.JSONUtil.toJsonStr(requestBody))
                    .timeout(30000)
                    .execute();

            // 解析AI返回结果
            cn.hutool.json.JSONObject json = cn.hutool.json.JSONUtil.parseObj(response.body());
            reply = json.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getStr("content");
        } catch (Exception e) {
            // 如果AI接口调用失败，使用本地兜底回复
            reply = getLocalReply(content);
        }

        // 保存对话记录
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setContent(content);
        message.setReply(reply);
        chatMessageMapper.insert(message);

        Map<String, Object> data = new HashMap<>();
        data.put("reply", reply);
        return Result.success(data);
    }

    /**
     * 我的对话记录
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> history(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getUserId, userId)
                        .orderByDesc(ChatMessage::getCreateTime)
                        .last("LIMIT 50")));
    }

    /**
     * 本地兜底回复（AI接口不可用时使用）
     */
    private String getLocalReply(String content) {
        if (content.contains("领养") || content.contains("怎么领养")) {
            return "您好！领养流程很简单：\n1. 在「宠物中心」浏览可领养的宠物\n2. 点击喜欢的宠物进入详情页\n3. 填写领养申请（包括您的居住地址和养宠经验）\n4. 等待管理员审核通过即可\n\n请问您有想领养的宠物类型吗？";
        } else if (content.contains("疫苗") || content.contains("打针")) {
            return "幼宠一般在45天龄后开始接种疫苗：\n- 猫：猫三联（预防猫瘟、猫鼻支、猫杯状）+ 狂犬疫苗\n- 狗：犬四联/六联 + 狂犬疫苗\n\n建议每年加强免疫一次，领养后我们会提供宠物的疫苗记录哦。";
        } else if (content.contains("寄养") || content.contains("临时照顾")) {
            return "我们提供宠物寄养服务！您可以在「寄养申请」页面提交申请，填写寄养时间和宠物信息，管理员会为您安排合适的房间。寄养期间我们会记录宠物的健康状况，请您放心。";
        } else if (content.contains("流浪") || content.contains("捡到")) {
            return "如果您发现了流浪动物，请：\n1. 注意自身安全，不要强行抓捕\n2. 在「流浪上报」页面填写发现地点和描述\n3. 如有可能，拍几张清晰的照片上传\n4. 留下您的联系方式，我们会尽快安排救助";
        } else if (content.contains("你好") || content.contains("hi") || content.contains("在吗")) {
            return "您好！我是宠物救助助手小助手，可以回答您关于领养、寄养、宠物护理、流浪动物救助等问题。请问有什么可以帮您的？";
        } else {
            return "感谢您的咨询！关于「" + content + "」这个问题，建议您联系我们的工作人员获取详细解答。您也可以在公告栏查看最新通知，或者拨打救助热线咨询。";
        }
    }
}
