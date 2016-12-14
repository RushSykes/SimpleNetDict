package Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class WordPic {
    public static String createImage
            (String str, Font font, File outFile,
             Integer width, Integer height)
            throws Exception {

        // Create picture
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_BGR);

        Graphics g = image.getGraphics();

        // Fill background with Pure white
        g.setClip(0, 0, width, height);
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        // Set font & color
        g.setColor(Color.black);
        g.setFont(font);

        // Get FontMetrics
        FontMetrics fm = g.getFontMetrics(font);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        String[] temp = str.split("\n");

        // Draw string on the canvass
        for(int i = 0; i < temp.length; i++) {
            g.drawString(temp[i], 0, 24 * (i + 1));
        }

        g.dispose();
        // Save as a file on the disk
        ImageIO.write(image, "png", outFile);
        return outFile.toString();
    }
}
