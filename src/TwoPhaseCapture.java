import ij.plugin.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.lang.Float;
import ij.*;
import ij.gui.*;
import ij.io.*;
import ij.process.*;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.Analyzer;
import ij.measure.*;
import ij.measure.Measurements;
import ij.plugin.frame.ThresholdAdjuster;
import ij.plugin.Thresholder;
import ij.process.ImageProcessor;
import ij.plugin.filter.Binary;
import ij.plugin.filter.Analyzer;
import ij.io.FileSaver;
import ij.plugin.Thresholder;
import ij.plugin.filter.BackgroundSubtracter;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
public class TwoPhaseCapture implements PlugInFilter, Measurements,PlugIn{

ImagePlus   imp;
Calibration cal;
static double   framesPerSecond = 1000.0;
static int theta=90;
static int maxSize=1000;
static int minSize=1;
static boolean  calibrate = true;
public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        cal = imp.getCalibration();
        if (IJ.versionLessThan("1.17y"))
            return DONE;
        else
            return DOES_8G+NO_CHANGES;
    }
final static boolean setup()
	{
		final GenericDialog gd = new GenericDialog( "Setup" );
		gd.addNumericField( "minSize : ", minSize, 8,12,"pixel"  );
		gd.addNumericField( "maxSize: ", maxSize, 8,12,"pixel");
		gd.addNumericField( "Frame per second : ", framesPerSecond,8,12,"pixel" );
		gd.addCheckbox("Calibrate", calibrate);
		
		gd.showDialog();
		
		if ( gd.wasCanceled() ) return false;
		
		minSize = ( int )gd.getNextNumber();
		maxSize = ( int )gd.getNextNumber() - 1;
		framesPerSecond = ( double )gd.getNextNumber();
		
		return true;
	}

public void run(String args) {
      
      OpenDialog od = new OpenDialog("Open.....", args);       
      String directory = od.getDirectory();
      String filename = od.getFileName();
      String processed_files="processed_photos";
      // new directory to write results
      File f = new File(directory+"/"+processed_files);
      if (f.mkdir()) {
  
           
            System.out.println("Directory is created");
                    }
      else {
          
            System.out.println("Directory cannot be created");
        }

        FolderOpener fo = new FolderOpener(); // create FolderOpener object
        ImagePlus imp =fo.openFolder(directory);

		    ImageStack stack = imp.getStack();
        IJ.showMessage(String.valueOf(stack.getSize()));
        int x = stack.getWidth();
        int y = stack.getHeight();
        int z = stack.getSize();
        ResultsTable rt = new ResultsTable();
        ResultsTable summary = new ResultsTable();
        //parameters to be extracted 
        int measurements = CENTROID+AREA+RECT+PERIMETER+FERET;
        //do you want to see a detailed report, summary, outlines...?
        int options=ParticleAnalyzer.SHOW_SUMMARY+ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES+ParticleAnalyzer.SHOW_OUTLINES;
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt,10,100000);
        pa.setSummaryTable(summary);
        rt.reset(); 
        for (int i=1; i<=stack.getSize(); i++) {
                       
                  	ImageProcessor ip = stack.getProcessor(i);
                    ImagePlus selection = new ImagePlus("frame_"+i, ip);
                    // if you would like to crop the image 
                   //you may use the code below instead while selecting the pixels you want to process
                    //ip.setRoi(10,10,x-20,y-20);
                    //ImagePlus selection = new ImagePlus("frame_"+i, ip.crop());
                    new FileSaver(selection).saveAsJpeg(directory+"/"+processed_files+"/frame_cropped_"+i);
                    IJ.run(selection,"Subtract Background...", "rolling=100 light sliding");
                    IJ.setRawThreshold(selection, 0, 185, null);
                    IJ.run(selection, "Convert to Mask", "");  
                    IJ.run(selection,"Fill Holes", "");
                    IJ.run(selection, "Convert to Mask", "");
                    IJ.run(selection, "Watershed", "");
                    //using particle analyzer utility in ImageJ
                    pa.analyze(selection, ip);
                    new FileSaver(selection).saveAsJpeg(directory+"/"+processed_files+"/frame_result_"+i);
                    summary.addValue("Slice",String.valueOf(i));
                    rt.save(directory+"/frame_result_"+i+".csv");
                    IJ.save(directory+"/"+processed_files+"/frame_outline_"+i+".jpeg");
         
          
       
      

    }

   summary.save(directory+"summary.csv");  
        
		

            
   




}
public void run(ImageProcessor ip) {


}
// run the macro from main
public static void main(String[] args) throws Exception {
TwoPhaseCapture p=new TwoPhaseCapture();
p.run("");


    }



}
