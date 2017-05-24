package com.sdcloud.web.lar.util.kaptcha;

import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.util.Configurable;
import java.awt.image.BufferedImage;

/**
* @author jzc
* @version 2016年9月12日 下午2:22:25
* NoWater描述: 该图片样式 是 没有做任何渲染的样式
* 图片样式：自带的有 	
*   水纹 com.google.code.kaptcha.impl.WaterRipple 
*   鱼眼com.google.code.kaptcha.impl.FishEyeGimpy
*   阴影com.google.code.kaptcha.impl.ShadowGimpy
*/
public class NoWater extends Configurable
  implements GimpyEngine
{
  public BufferedImage getDistortedImage(BufferedImage baseImage)
  {
    return baseImage;
  }
}
