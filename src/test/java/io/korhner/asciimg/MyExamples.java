/**
 * @(#)io.korhner.asciimg.MyExamples.java Copyright (c) 2014-2018 crimps
 */
package io.korhner.asciimg;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.converter.AsciiToImageConverter;
import io.korhner.asciimg.image.converter.AsciiToStringConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

/**
 *
 * @author crimps
 * @version 1.0  2017/3/15
 * @modified crimps  2017/3/15  <创建>
 */
public class MyExamples {

    public static void main(String[] args) throws Exception{
        AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier", Font.BOLD, 6));
        BufferedImage portraitImage = resize(ImageIO.read(new File("examples/3.jpg")), 120, 170);

        // initialize algorithms
        BestCharacterFitStrategy squareErrorStrategy = new ColorSquareErrorFitStrategy();
        BestCharacterFitStrategy ssimStrategy = new StructuralSimilarityFitStrategy();

        // initialize converters
        AsciiToImageConverter imageConverter = new AsciiToImageConverter(
                smallFontCache, squareErrorStrategy);
        AsciiToStringConverter stringConverter = new AsciiToStringConverter(smallFontCache, ssimStrategy);

        // string converter, output to console
        System.out.println(stringConverter.convertImage(portraitImage));

    }

    /**
     * 实现图像的等比缩放
     * @param source
     * @param targetW
     * @param targetH
     * @return
     */
    private static BufferedImage resize(BufferedImage source, int targetW,
                                        int targetH) {
        // targetW，targetH分别表示目标长和宽
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        // 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
        // 则将下面的if else语句注释即可
        if (sx < sy) {
            sx = sy;
            targetW = (int) (sx * source.getWidth());
        } else {
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }
        if (type == BufferedImage.TYPE_CUSTOM) { // handmade
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
                    targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else
            target = new BufferedImage(targetW, targetH, type);
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }
}