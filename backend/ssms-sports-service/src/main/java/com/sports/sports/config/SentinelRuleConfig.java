package com.sports.sports.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 规则配置模版
 * 用于在代码中硬编码初始防护规则，防止“裸跑”
 */
@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    /**
     * 限流规则: 防止突发大流量压垮系统
     */
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        // 资源名称，通常对应 Feign 接口或 Controller 路径
        rule.setResource("com.sports.sports.client.UserClient:listByIds(java.util.List)");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 每秒允许的最大请求数 (演示设置为 100)
        rule.setCount(100);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 熔断降级规则: 防止下游服务异常拖垮当前服务
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("com.sports.sports.client.UserClient:listByIds(java.util.List)");
        // 熔断策略: 异常比例 (DEGRADE_GRADE_EXCEPTION_RATIO)
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        // 异常比例阈值: 0.5 表示 50% 的请求异常时触发熔断
        rule.setCount(0.5);
        // 熔断时长 (秒): 熔断后 10 秒内直接走 Fallback
        rule.setTimeWindow(10);
        // 最小请求数: 样本量达到 5 个后才开始计算比例
        rule.setMinRequestAmount(5);
        
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }
}
