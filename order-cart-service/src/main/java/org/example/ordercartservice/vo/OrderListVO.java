package org.example.ordercartservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.ordercartservice.vo.LogisticsSimpleVO;
import org.example.ordercartservice.vo.OrderItemPreviewVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "订单列表视图对象")
public class OrderListVO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单号", example = "20240301123456789")
    private String orderNo;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String userName;

    @Schema(description = "用户头像", example = "http://image.luoye.com/avatar/1001.jpg")
    private String userAvatar;

    @Schema(description = "订单总金额", example = "6999.00")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额", example = "6799.00")
    private BigDecimal payAmount;

    @Schema(description = "运费", example = "0.00")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额", example = "200.00")
    private BigDecimal discountAmount;

    @Schema(description = "支付方式", example = "1")
    private Integer payType;

    @Schema(description = "支付方式文字", example = "微信支付")
    private String payTypeText;

    @Schema(description = "订单状态", example = "0")
    private Integer status;

    @Schema(description = "订单状态文字", example = "待付款")
    private String statusText;

    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @Schema(description = "收货人电话", example = "13800138000")
    private String receiverPhone;

    @Schema(description = "收货人地址", example = "北京市朝阳区xxx街道xxx号")
    private String receiverAddress;

    @Schema(description = "订单备注", example = "请放快递柜")
    private String remark;

    @Schema(description = "商品总数量", example = "3")
    private Integer totalCount;

    @Schema(description = "商品总种类", example = "2")
    private Integer totalKind;

    @Schema(description = "订单商品列表（预览用）")
    private List<OrderItemPreviewVO> items;

    @Schema(description = "首图（用于列表展示）", example = "http://image.luoye.com/product/1/main.jpg")
    private String firstImage;

    @Schema(description = "首商品名称", example = "华为Mate 60 Pro")
    private String firstProductName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    @Schema(description = "收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

    @Schema(description = "取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    @Schema(description = "倒计时（秒）", example = "1800")
    private Long countdown;

    @Schema(description = "物流信息")
    private LogisticsSimpleVO logistics;

    @Schema(description = "操作按钮列表")
    private List<String> actions;

    /**
     * 获取支付方式文字
     */
    public String getPayTypeText() {
        if (payType == null) return "";
        switch (payType) {
            case 1: return "微信支付";
            case 2: return "支付宝支付";
            case 3: return "银联支付";
            default: return "未知";
        }
    }

    /**
     * 获取订单状态文字
     */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "售后中";
            default: return "未知";
        }
    }

    /**
     * 计算倒计时（待付款订单）
     */
    public Long getCountdown() {
        if (status == 0 && createTime != null) {
            // 假设30分钟未支付自动取消
            LocalDateTime expireTime = createTime.plusMinutes(30);
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(expireTime)) {
                return java.time.Duration.between(now, expireTime).getSeconds();
            }
        }
        return 0L;
    }

    /**
     * 获取可操作按钮列表
     */
    public List<String> getActions() {
        if (status == null) return List.of();

        switch (status) {
            case 0: // 待付款
                return List.of("pay", "cancel");
            case 1: // 已付款
                return List.of("track", "applyRefund");
            case 2: // 已发货
                return List.of("confirm", "track");
            case 3: // 已完成
                return List.of("review", "again", "afterSale");
            case 4: // 已取消
                return List.of("delete", "again");
            case 5: // 售后中
                return List.of("track", "contact");
            default:
                return List.of();
        }
    }
}