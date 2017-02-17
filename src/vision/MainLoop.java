package vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class MainLoop extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	int i = 0;
	long starttime;

	private final int frames = 10;

	double tempMaxX, tempMaxY, tempMinX, tempMinY;

	GripPipeline gp;
	GripPipelineWide gp2;

	public MainLoop() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int update = 1000 / frames;
		Timer time = new Timer(update, this);
		time.start();

		gp = new GripPipeline();
		gp2 = new GripPipelineWide();

		starttime = System.currentTimeMillis();

		// test1();
	}

	public void testing() {
		String imgname = "test.jpg";
		Mat img = Imgcodecs.imread(imgname);
		gp.process(img);

		Iterator<MatOfPoint> it = gp.convexHullsOutput().iterator();

		while (it.hasNext())
			System.out.println(it.next());

		org.opencv.core.Point points[] = new org.opencv.core.Point[gp.convexHullsOutput().size()];

		points = gp.convexHullsOutput().get(0).toArray();
		System.out.println(gp.convexHullsOutput().size());

		for (Point i : points)
			System.out.println("x: " + i.x + "\ny: " + i.y + "\n");

		/*
		 * MatOfPoint2f curve = new MatOfPoint2f(); for (int i = 0; i <
		 * gp.convexHullsOutput().size(); i++) { MatOfPoint2f cont2f = new
		 * MatOfPoint2f(gp.convexHullsOutput().get(i).toArray()); double
		 * approxDistance = Imgproc.arcLength(cont2f, true)*0.02;
		 * Imgproc.approxPolyDP(cont2f, curve, approxDistance, true); MatOfPoint
		 * mp = new MatOfPoint(curve.toArray()); Rect rect =
		 * Imgproc.boundingRect(mp); Core.rectangle( ,new Point(rect.x,rect.y),
		 * new Point(rect.x+rect.width,rect.y+rect.height), (255, 0, 0, 255),
		 * 3); }
		 */
	}
 
	public void paintComponent(Graphics g) {
		Graphics g2d = (Graphics) g;
		super.paintComponent(g2d);
		System.out.println("time " + ((System.currentTimeMillis() - starttime)));
		/*
		 * g2d.drawRect(i, i, 250+i, 250+i); System.out.println("running: " +
		 * i); i++;
		 */

		// String imgname = "test.jpg";
		// Mat img1 = Imgcodecs.imread(imgname);

		BufferedImage input = wizardry();

		if (input != null)
			g2d.drawImage(input, 0, 0, null);

		if (gp2.convexHullsOutput().size() != 0) {
			org.opencv.core.Point points[] = new org.opencv.core.Point[gp2.convexHullsOutput().size()];
			//System.out.println(points.length);

			points = gp2.convexHullsOutput().get(0).toArray();

			for (Point i : points)
				g2d.drawOval((int) i.x, (int) i.y, 5, 5);

			tempMaxX = points[0].x;
			tempMaxY = points[0].y;
			tempMinX = points[0].x;
			tempMinY = points[0].y;

			Point maxPoint = null, minPoint = null;
			double tempDistMin = 10000, tempDistMax = 0;
			double tempDist = 0;

			for (int i = 0; i < points.length; i++) {
				tempDist = Math.sqrt(Math.pow(points[i].x, 2) + Math.pow(points[i].y, 2));
				// System.out.println(tempDist);
				if (tempDist < tempDistMin) {
					tempDistMin = tempDist;
					minPoint = points[i].clone();
				} else if (tempDist > tempDistMax) {
					tempDistMax = tempDist;
					maxPoint = points[i].clone();
				}
			}

			// System.out.println("\n\n\nPoints:");
			// System.out.println("Min: " + minPoint.x + ", " + minPoint.y);
			// System.out.println("Max: " + maxPoint.x + ", " + maxPoint.y);
			g2d.setColor(Color.GREEN);
			g2d.fillRect((int) minPoint.x, (int) minPoint.y, (int) (maxPoint.x - minPoint.x),
					(int) (maxPoint.y - minPoint.y));
			
/*			g2d.drawLine((int)minPoint.x, (int)minPoint.y, (int)maxPoint.x, (int)maxPoint.y);
			g2d.drawOval((int) ((minPoint.x + maxPoint.x)/2), (int) ((minPoint.y + maxPoint.y)/2), 50, 50);*/
		}
	}

	public BufferedImage wizardry() {

		VideoCapture camera = new VideoCapture(0);
		Mat frame = new Mat();

		camera.read(frame);

		gp2.process(frame);
		MatOfByte mob = new MatOfByte();

		// gp.process(img1);

		Imgcodecs.imencode(".jpg", gp2.cvResizeOutput(), mob);
		byte[] byteArray = mob.toArray();
		BufferedImage bi = null;

		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bi = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bi;
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		repaint();
	}

}
