package com.jwt.freecloud.util;

import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.UserConstants;
import com.jwt.freecloud.common.entity.UserLevel;
import com.jwt.freecloud.common.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author：揭文滔
 * @since：2023/3/15
 */
public class CheckUtil {

    /**
     * 手机号校验正则
     */
    public static final String PHONE_RULES = "^[1][3,4,5,6,7,8,9][0-9]{9}$";

    /**
     * 判断手机号是否合法
     * @param phone
     * @return
     */
    public static boolean isRightPhone(String phone) {
        return phone.matches(PHONE_RULES);
    }

    /**
     * JSR303校验返回错误结果
     * @param result
     * @return
     */
    public static FreeResult returnErrors(BindingResult result){
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return FreeResult.fail().putData("errors",errors);
        }
        return null;
    }

    /**
     * 检验用户剩余容量是否大于该文件大小
     * @param userVO
     * @param fileSize
     * @return
     */
    public static boolean checkMemoryEnough(UserVO userVO, Long fileSize) {

        long remainMemory = userVO.getTotalMemory() - userVO.getUsedMemory();
        return remainMemory >= fileSize;

    }

    /**
     * 更新经验值，判断是否能升级
     * @param userLevel
     */
    public static void checkLevelByIncreaseExp(UserLevel userLevel) {
        Integer upgradeExp = userLevel.getUpgradeExp();
        Integer currentExp = userLevel.getCurrentExp();
        Integer level = userLevel.getLevel();
        currentExp += FileConstants.UPLOAD_EXP;
        if (currentExp > upgradeExp) {
            //升级
            currentExp -= upgradeExp;
            level += 1;
            upgradeExp = level * UserConstants.USER_UPGRADE_EXP;
        }
        userLevel.setLevel(level);
        userLevel.setUpgradeExp(upgradeExp);
        userLevel.setCurrentExp(currentExp);
    }

    public static String[] checkShareUrl(String shareUrl) {
        if (shareUrl == null || "".equals(shareUrl)) {
            return null;
        }
        // 根据规则，shareUrl由32位的shareIdentify和shareId组成，中间由'_'分隔
        String[] s = shareUrl.split("_");
        if (s.length == 1) {
            return null;
        }
        // 长度不符合
        if (s[0].length() != 32) {
            return null;
        }
        // 不是数字
        if (!StringUtils.isNumeric(s[1])) {
            return  null;
        }
        return s;
    }
}
