using Emgu.CV;
using Emgu.CV.Structure;
using System;
using Emgu.CV.CvEnum;
using Emgu.CV.Cuda;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing.Drawing2D;

namespace Images
{
    public partial class Form1 : Form
    {
        public static VideoCapture capt;
        public static int levels;
        public static int drawVector;
        public static int[,] xywh = new int[2, 2];
        //public static int totalFrames;
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }
        private void button1_Click(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "Video Files (*.mp4, *.flv)| *.mp4;*.flv";
            if (ofd.ShowDialog() == DialogResult.OK)
            {
                capt = new VideoCapture(ofd.FileName); // в папке \Images\bin\Debug\
            }

            //capt = new VideoCapture("blacks.mp4");   // testv.mp4"
        }
        private void button2_Click(object sender, EventArgs e)
        {
            this.Hide();
            Form2 frm2 = new Form2();
            frm2.ShowDialog();
            this.Close();

        }

        private void button3_Click(object sender, EventArgs e)
        {
            int height, width, widthIntSmall, heightIntSmall;
            levels = int.Parse(textBox1.Text);
            Mat frame = new Mat();

            capt.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.PosFrames, 0);
            capt.Read(frame);

            height = frame.Height;
            width = frame.Width;
            widthIntSmall = (int)Math.Ceiling(width / Math.Pow(2, levels - 1));
            heightIntSmall = (int)Math.Ceiling(height / Math.Pow(2, levels - 1));

            if (listBox1.Items.Count != 0)
            {
                listBox1.Items.Clear();
                listBox2.Items.Clear();
            }
            List<int> dividers = FindDividers(widthIntSmall);
            for (int i = 0; i < dividers.Count; i++)
                listBox1.Items.Add(dividers[i]);
            dividers = FindDividers(heightIntSmall);
            for (int i = 0; i < dividers.Count; i++)
                listBox2.Items.Add(dividers[i]);

        }

        private List<int> FindDividers(int wh)
        {
            var list = new List<int>();
            for(int i = wh; i > 1; i--)
            {
                if (wh % i == 0)
                    list.Add(i);
            }
            return list;
        }

        private void button4_Click(object sender, EventArgs e)
        {
            xywh[0, 0] = 0;
            xywh[0, 1] = 0;
            xywh[1, 0] = Convert.ToInt32(listBox1.SelectedItem); // ширина мин блока
            xywh[1, 1] = Convert.ToInt32(listBox2.SelectedItem); // высота мин блока

        }

        private void button5_Click(object sender, EventArgs e)
        {
            drawVector = int.Parse(textBox2.Text);
        }

    }
}
    