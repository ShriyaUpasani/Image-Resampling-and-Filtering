//javac ImageDisplay.java; java ImageDisplay "C:\Shriya\MSD\Assignment1_starter_code_new\Assignment1_starter_code\aliasing_test_data_samples\aliasing_test1.rgb" 0.08 0 150
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;

public class ImageDisplay {
	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	BufferedImage Scaledimg;
	BufferedImage copyOfScaledimg;
	int width = 1920 * 4; // large image width and height
	int height = 1080 * 4;
	// int width = 1920; // default image width and height
	// int height = 1080 ;
	double scalingfactor; // Scaling factor
	static int newheight, newwidth;
	int w;
	double samplingRate;
	boolean ctrl = false;

	byte[] re;
	byte[] gr;
	byte[] bl;

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */

	private void readImageRGB(int width, int height, String imgPath, BufferedImage img) {
		try {
			int frameLength = width * height * 3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff); // preserves the 24 bit
																								// format for RGB
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
			int partSize = bytes.length / 3;
			re = Arrays.copyOfRange(bytes, 0, partSize);
			gr = Arrays.copyOfRange(bytes, partSize, 2 * partSize);
			bl = Arrays.copyOfRange(bytes, 2 * partSize, bytes.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showIms(String[] args) {

		try {
			// Read a parameter from command line
			String imagePath = args[0];
			scalingfactor = Double.parseDouble(args[1]); // Parse the scaling factor
			samplingRate = 1 / scalingfactor; // Calculate the sampling rate
			
			w = Integer.parseInt(args[3]); // Overlay w x w

			// Read the specified image
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			readImageRGB(width, height, imagePath, imgOne);

			// System.out.println("The scaling fac was: " + scalingfactor);
			// System.out.println("The sampling rate was: " + samplingRate);

			newwidth = (int) (width * scalingfactor);
			newheight = (int) (height * scalingfactor);


			// ANTI ALIASING CODE 
			if ("1".equals(args[2])) {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int Onlyred = 0;
						int Onlygreen = 0;
						int Onlyblue = 0;
						int count = 0;
						for (int kheight = -2; kheight <= 2; ++kheight) {
							for (int kwidth = -2; kwidth <= 2; ++kwidth) {
								int px = i + kwidth;
								int py = j + kheight;

								if (px >= 0 && px < width && py >= 0 && py < height) {
									int rgbb = imgOne.getRGB(px, py);
									int red = (rgbb >> 16) & 0xff;
									int green = (rgbb >> 8) & 0xff;
									int blue = (rgbb & 0xff);

									Onlyred += red;
									Onlygreen += green;
									Onlyblue += blue;
									count++;
								}
							}
						}
						int avgRed = Onlyred / count;
						int avgGreen = Onlygreen / count;
						int avgBlue = Onlyblue / count;

						int filteredRGB = (avgRed << 16) | (avgGreen << 8) | (avgBlue);
						imgOne.setRGB(i, j, filteredRGB);
					}
				}
			}

			// Scaling Code
			Scaledimg = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_RGB);
			int ind = 0;
			int scaledX = 0;
			int scaledY = 0;
			for (float y = 0; y < height; y += samplingRate, scaledY++) {
				scaledX = 0;
				for (float x = 0; x < width; x += samplingRate, scaledX++) {
					int px = (int) x;
					int py = (int) y;
					if (px >= 0 && px < width && py >= 0 && py < height && scaledX < newwidth && scaledX >= 0
							&& scaledY < newheight && scaledY >= 0) {
						int pixe = imgOne.getRGB(px, py);
						Scaledimg.setRGB(scaledX, scaledY, pixe);
					}
				}
			}

			//Copy of scaled Image
			copyOfScaledimg = new BufferedImage(Scaledimg.getWidth(), Scaledimg.getHeight(),Scaledimg.getType());
			Graphics g = copyOfScaledimg.getGraphics();
			g.drawImage(Scaledimg, 0, 0, null);
			g.dispose();
		

			// Use label to display the image
			frame = new JFrame();
			GridBagLayout gLayout = new GridBagLayout();
			frame.getContentPane().setLayout(gLayout);
			lbIm1 = new JLabel(new ImageIcon(Scaledimg));

			//Check if Control Key is pressed
			frame.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_CONTROL && !ctrl) {
						ctrl = true;
					}
				}

				public void keyReleased(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_CONTROL && ctrl) {
						ctrl = false;
						reDraw();
						frame.repaint();
					}
				}

			});

			//Track mouse movement and display overlay only when control key is pressed
			frame.addMouseMotionListener(new MouseMotionAdapter() {
				// @Override
				public void mouseMoved(MouseEvent e) {
					// Get the mouse coordinates
					int mouseX = e.getX();
					int mouseY = e.getY();

					reDraw();
					if (ctrl) {
						displayOverlay(mouseX - 8, mouseY - 32);
					}
					frame.repaint();
				}
			});

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			frame.getContentPane().add(lbIm1, c);

			frame.pack();
			frame.setVisible(true);
			frame.setFocusable(true);

			frame.setResizable(false);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	//Repaint the image here
	public void reDraw() {
		Graphics g = Scaledimg.getGraphics();
		g.drawImage(copyOfScaledimg, 0, 0, null);
		g.dispose();
	}

	//Display the OverLay
	public void displayOverlay(int mousex, int mousey) {
		try {

			int offset = w / 2;

			// Map mouse coordinate to original image coordinates
			int originalX = (int) (mousex * samplingRate); // mousex coord in original image
			int originalY = (int) (mousey * samplingRate); //mousey coordinate in original image
			int kx = originalX - offset;// original image top left
			int ky = originalY - offset;//original image top left
			int sx = mousex - offset;// scaled img top left
			int sy = mousey - offset; //scaled img top left

			for (int x = kx; x <= kx + w; x++, sx++) {
				sy = mousey - offset;
				for (int y = ky; y <= ky + w; y++, sy++) {
					int rgbb = imgOne.getRGB(x, y);

					int red = (rgbb >> 16) & 0xff;
					int green = (rgbb >> 8) & 0xff;
					int blue = (rgbb & 0xff);
					int overlayedRGB = (red << 16) | (green << 8) | (blue);
					if (sx > 0 && sy > 0 && sx < newwidth && sy < newheight) {
						Scaledimg.setRGB(sx, sy, overlayedRGB);
					}
				}
			}
			frame.getContentPane().repaint();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
