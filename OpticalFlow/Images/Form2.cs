using Emgu.CV;
using Emgu.CV.Structure;
using Emgu.CV.CvEnum;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Images
{
    public partial class Form2 : Form
    {
        List<Image<Bgr, Byte>> imagesList = new List<Image<Bgr, Byte>>();
        static object locker = new object();
        int fps;
        public Form2()
        {
            InitializeComponent();
        }

        private void Video_Load(object sender, EventArgs e)
        {

        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            
            VideoCapture capt = Form1.capt;
            int levels = Form1.levels;
            var xywh = Form1.xywh;
            int drawVector = Form1.drawVector;

            double delitel = Math.Pow(2, levels - 1);
            int extra = 256;

            int height, width;
            int totalFrames;
            Mat frame = new Mat();
            totalFrames = Convert.ToInt32(capt.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.FrameCount));
            fps = Convert.ToInt32(capt.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.Fps));

            capt.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.PosFrames, 0);
            capt.Read(frame);
            Image<Bgr, Byte> img3 = frame.ToImage<Bgr, Byte>();

            height = img3.Height;
            width = img3.Width;
            int widthInt = (int)Math.Ceiling(width / delitel) * (int)delitel;
            int heightInt = (int)Math.Ceiling(height / delitel) * (int)delitel;

            Image<Bgr, Byte> imgAddW = new Image<Bgr, Byte>(widthInt - width, height, new Bgr(255, 255, 255));
            Image<Bgr, Byte> imgAddH = new Image<Bgr, Byte>(width, heightInt - height, new Bgr(255, 255, 255));
            Image<Bgr, Byte> imgAddS = new Image<Bgr, Byte>(widthInt - width, heightInt - height, new Bgr(255, 255, 255));

            Image<Bgr, Byte> imgHor = new Image<Bgr, Byte>(extra, heightInt, new Bgr(255, 255, 255));
            Image<Bgr, Byte> imgVer = new Image<Bgr, Byte>(widthInt + 2 * extra, extra, new Bgr(255, 255, 255));

            Mat matAddSW = new Mat();


            /*********************            *********************             *********************/
             totalFrames = 2;
            for (int k = 0; k < totalFrames-1; k++)
            {
                capt.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.PosFrames, k);
                capt.Read(frame);
                if (widthInt - width != 0 && heightInt - height != 0)
                {
                    CvInvoke.VConcat(frame, imgAddH, frame);
                    CvInvoke.VConcat(imgAddW, imgAddS, matAddSW);
                    CvInvoke.HConcat(frame, matAddSW, frame);
                }
                else if (widthInt - width != 0)
                    CvInvoke.HConcat(frame, imgAddW, frame);
                else if (heightInt - height != 0)
                    CvInvoke.VConcat(frame, imgAddH, frame);

                CvInvoke.HConcat(imgHor, frame, frame);
                CvInvoke.HConcat(frame, imgHor, frame);
                CvInvoke.VConcat(imgVer, frame, frame);
                CvInvoke.VConcat(frame, imgVer, frame);

                Image<Gray, Byte> img1 = frame.ToImage<Gray, Byte>();
                CvInvoke.MedianBlur(img1, img1, 13);
                img3 = frame.ToImage<Bgr, Byte>();


                capt.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.PosFrames, k+1);
                capt.Read(frame);
                if (widthInt - width != 0 && heightInt - height != 0)
                {
                    CvInvoke.VConcat(imgAddH, frame, frame);
                    CvInvoke.VConcat(imgAddS, imgAddW, matAddSW);
                    CvInvoke.HConcat(frame, matAddSW, frame);
                }
                else if (widthInt - width != 0)
                    CvInvoke.HConcat(frame, imgAddW, frame);
                else if (heightInt - height != 0)
                    CvInvoke.VConcat(imgAddH, frame, frame);
                CvInvoke.HConcat(imgHor, frame, frame);
                CvInvoke.HConcat(frame, imgHor, frame);
                CvInvoke.VConcat(imgVer, frame, frame);
                CvInvoke.VConcat(frame, imgVer, frame);

                Image<Gray, Byte> img2 = frame.ToImage<Gray, Byte>();
                CvInvoke.MedianBlur(img2, img2, 13);


                Image<Gray, Byte>[,] images = new Image<Gray, Byte>[levels, 2];

                int heightC, widthC;
                heightC = img1.Height;
                widthC = img1.Width;
               

                images[0, 0] = img1;
                images[0, 1] = img2;

                for (int i = 1; i < levels; i++)
                {
                    widthC /= 2;
                    heightC /= 2;
                    images[i, 0] = img1.Resize(widthC, heightC, Inter.Linear);
                    images[i, 1] = img2.Resize(widthC, heightC, Inter.Linear);
                    CvInvoke.MedianBlur(images[i, 0], images[i, 0], 9);
                    CvInvoke.MedianBlur(images[i, 1], images[i, 1], 9);
                }

                int totalBlocksRows, totalBlocksColumns;
                widthC = widthInt / (int)delitel;  //ширина всех блоков на слое 2^(l-1)
                heightC = heightInt / (int)delitel;
                totalBlocksRows = (int)(heightC / (double)xywh[1, 1]) * (int)delitel;
                totalBlocksColumns = (int)(widthC / (double)xywh[1, 0]) * (int)delitel;

                var field = new int[totalBlocksRows, totalBlocksColumns][,];

                List<int[,]> inputList = new List<int[,]>();

                int extra2l = extra / (int)delitel;
                int xIn = extra2l, yIn = extra2l;
                while (yIn < heightC + extra2l)
                {
                    xIn = extra2l;
                    while (xIn < widthC + extra2l)
                    {
                        inputList.Add(new int[,] { { xIn, yIn }, { xywh[1, 0], xywh[1, 1] } });
                        xIn += xywh[1, 0];
                    }
                    yIn += xywh[1, 1];
                }

                var container = new List<List<int[,]>>();
                Parallel.ForEach(inputList, (o) =>
                {
                    var partField = new List<int[,]>();
                    var g = new[] { 0.0, 0.0 };
                    var g1 = new double[2];
                    var xy = new[,] { { o[0, 0], o[0, 1] }, { o[1, 0], o[1, 1] } };

                    g1 = CalcG(images, g, xy, levels - 1);
                    Recurs(images, partField, Gx2(g1), XYx2(xy), levels - 2);

                    lock (locker)
                    {
                        container.Add(partField);
                    }
                });
                FillField(container, field, xywh, extra);


                for (int i = 0; i < field.GetLength(0); i += drawVector)
                    for (int j = 0; j < field.GetLength(1); j += drawVector)
                    {
                        Point x = new Point(field[i, j][0, 0], field[i, j][0, 1]);
                        Point y = new Point(field[i, j][1, 0], field[i, j][1, 1]);
                        MCvScalar color = new MCvScalar(0, 255, 255);
                        try
                        {
                            CvInvoke.ArrowedLine(img3, x, y, color, 1, LineType.EightConnected, 0, 0.3);
                        }
                        catch
                        {
                            CvInvoke.ArrowedLine(img3, x, x, color, 1, LineType.EightConnected, 0, 0.3);
                        }

                    }

                img3.ROI = new Rectangle(extra, extra, width, height);
                imagesList.Add(img3);
            }
            /**********************************************************************/
            button3.Visible = true;
        }
        private async void button3_Click(object sender, EventArgs e)
        {
            for (int i = 0; i < imagesList.Count; i++)
            {
                imageBox1.Image = imagesList[i];
                await Task.Delay(1000 * 4 / fps);
            }
        }

        private void FillField(List<List<int[,]>> container, int[,][,] field, int[,] xywh, int extra)
        {
            int dw = xywh[1, 0];
            int dh = xywh[1, 1];
            int j, i;

            int[] g0 = { extra + dw / 2, extra + dh / 2 };

            foreach (var partField in container)
                foreach (var g in partField)
                {
                    j = (g[0, 0] - g0[0]) / dw;
                    i = (g[0, 1] - g0[1]) / dh;
                    field[i, j] = g;
                }
        }
        private double Ix(Image<Gray, Byte>[,] images, int x, int y, int level)
        {
            double I = (images[level, 0][y, x + 1].Intensity - images[level, 0][y, x - 1].Intensity) / 2.0;
            return I;
        }
        private double Iy(Image<Gray, Byte>[,] images, int x, int y, int level)
        {
            double I = (images[level, 0][y + 1, x].Intensity - images[level, 0][y - 1, x].Intensity) / 2.0;
            return I;
        }

        private double dIk(Image<Gray, Byte>[,] images, double[] g, double[] vk, int x, int y, int level)
        {
            int degree = 1;
            int layer = level;
            double I, C;

            if (level == 0)
            {
                degree = 0; layer = 1;
            }
            C = Math.Pow(2, degree);

            int gxPLvx = (int)Math.Round(C * (g[0] + vk[0]));
            int gyPLvy = (int)Math.Round(C * (g[1] + vk[1]));

            x = (int)C * x;
            y = (int)C * y;
            int h = images[layer - 1, 0].Height, w = images[layer - 1, 0].Width;

            if (y + gyPLvy < 0 || y + gyPLvy >= h || x + gxPLvx < 0 || x + gxPLvx >= w)
                return 0;

            I = images[layer - 1, 0][y, x].Intensity - images[layer - 1, 1][y + gyPLvy, x + gxPLvx].Intensity;
            return I;
        }

        private double[,] bk(Image<Gray, Byte>[,] images, double[] g, double[] vk, int[,] xywh, int level, double[,] W)
        {
            var b = new double[2, 1];
            double Ik;
            for (int j = xywh[0, 1]; j < xywh[1, 1] + xywh[0, 1]; j++)
                for (int i = xywh[0, 0]; i < xywh[1, 0] + xywh[0, 0]; i++)
                {
                    Ik = dIk(images, g, vk, i, j, level);
                    b[0, 0] += Ik * Ix(images, i, j, level) * W[j - xywh[0, 1], i - xywh[0, 0]];
                    b[1, 0] += Ik * Iy(images, i, j, level) * W[j - xywh[0, 1], i - xywh[0, 0]];
                }
            return b;
        }
        private double[,] w(Image<Gray, Byte>[,] images, int[,] xywh, int level)
        {
            var w = new double[xywh[1, 1], xywh[1, 0]];
            int x0 = xywh[0, 0] + xywh[1, 0] / 2;
            int y0 = xywh[0, 1] + xywh[1, 1] / 2;

            int sigD = 19, sigC = 21;
            int di, dj;
            double wd, wc;

            for (int j = xywh[0, 1]; j < xywh[1, 1] + xywh[0, 1]; j++)
                for (int i = xywh[0, 0]; i < xywh[1, 0] + xywh[0, 0]; i++)
                {
                    di = i - x0;
                    dj = j - y0;
                    wd = Math.Exp(-(di * di + dj * dj) / (2.0 * sigD * sigD));
                    wc = Math.Exp(-Math.Pow(images[level, 0][j, i].Intensity - images[level, 0][y0, x0].Intensity, 2) / (2.0 * sigC * sigC));
                    w[j - xywh[0, 1], i - xywh[0, 0]] = wd * wc;
                }
            return w;
        }
        private double[] CalcG(Image<Gray, Byte>[,] images, double[] g, int[,] xywh, int level)
        {
            var vk = new double[2];

            if (xywh[0, 0] == 1488 && xywh[0, 1] == 292)
            {
                xywh[0, 0] += 0;
            }

            var b = new double[2, 1];
            var W = w(images, xywh, level);
            int isInvertible = 1;
            var G = G_inv(images, xywh, level, W, ref isInvertible);

            if (isInvertible == 0)
            {
                vk[0] += g[0];
                vk[1] += g[1];
                return vk;
            }

            for (int k = 0; k < 5; k++)
            {
                b = bk(images, g, vk, xywh, level, W);
                vk[0] += G[0, 0] * b[0, 0] + G[0, 1] * b[1, 0];
                vk[1] += G[1, 0] * b[0, 0] + G[1, 1] * b[1, 0];
            }
            vk[0] += g[0];
            vk[1] += g[1];
            return vk;
        }

        private double[,] G_inv(Image<Gray, Byte>[,] images, int[,] xywh, int level, double[,] W, ref int isInvertible)
        {
            var G = new double[2, 2];
            double temp, det;
            double intx, inty;
            for (int j = xywh[0, 1]; j < xywh[1, 1] + xywh[0, 1]; j++)
                for (int i = xywh[0, 0]; i < xywh[1, 0] + xywh[0, 0]; i++)
                {
                    intx = Ix(images, i, j, level);
                    inty = Iy(images, i, j, level);
                    G[0, 0] += intx * intx * W[j - xywh[0, 1], i - xywh[0, 0]];
                    G[1, 1] += inty * inty * W[j - xywh[0, 1], i - xywh[0, 0]];
                    G[0, 1] += intx * inty * W[j - xywh[0, 1], i - xywh[0, 0]];
                    G[1, 0] = G[0, 1];
                }
            temp = G[0, 0];
            det = G[0, 0] * G[1, 1] - G[1, 0] * G[1, 0];
            if (det == 0)
            {
                det = 1;
                isInvertible = 0;
            }

            G[0, 0] = G[1, 1] / det;
            G[1, 1] = temp / det;
            G[0, 1] = G[0, 1] * (-1) / det;
            G[1, 0] = G[0, 1];
            return G;
        }

        private void Recurs(Image<Gray, Byte>[,] images, List<int[,]> partFieldU, double[] g, int[,] xywh, int level)  //List<int[,]> partFieldU
        {
            if (level == -1)
            {
                int x = (xywh[0, 0] + xywh[1, 0]) / 2;
                int y = (xywh[0, 1] + xywh[1, 1]) / 2;

                partFieldU.Add(new[,] { { x, y }, { x + (int)Math.Round(g[0] / 2), y + (int)Math.Round(g[1] / 2) } });
                return;
            }
            var xywhRecurs = new int[,] { { xywh[0, 0], xywh[0, 1] }, { xywh[1, 0], xywh[1, 1] } };
            var currentG = new double[2];

            currentG = CalcG(images, g, xywhRecurs, level);
            Recurs(images, partFieldU, Gx2(currentG), XYx2(xywhRecurs), level - 1);


            XYdivide2(xywhRecurs);
            xywhRecurs[0, 0] += xywhRecurs[1, 0];
            currentG = CalcG(images, g, xywhRecurs, level);
            Recurs(images, partFieldU, Gx2(currentG), XYx2(xywhRecurs), level - 1);


            XYdivide2(xywhRecurs);
            xywhRecurs[0, 1] += xywhRecurs[1, 1];
            currentG = CalcG(images, g, xywhRecurs, level);
            Recurs(images, partFieldU, Gx2(currentG), XYx2(xywhRecurs), level - 1);


            XYdivide2(xywhRecurs);
            xywhRecurs[0, 0] -= xywhRecurs[1, 0];
            currentG = CalcG(images, g, xywhRecurs, level);
            Recurs(images, partFieldU, Gx2(currentG), XYx2(xywhRecurs), level - 1);
        }
        private int[,] XYdivide2(int[,] xywh)
        {
            xywh[0, 0] /= 2;
            xywh[0, 1] /= 2;
            return xywh;
        }
        private int[,] XYx2(int[,] xywh)
        {
            xywh[0, 0] *= 2;
            xywh[0, 1] *= 2;
            return xywh;
        }

        private double[] Gx2(double[] g)
        {
            g[0] = 2 * g[0];
            g[1] = 2 * g[1];
            return g;
        }

        private void imageBox1_Click(object sender, EventArgs e)
        {

        }
    }
}
