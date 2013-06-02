package ch.ethz.naro;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.ros.node.topic.Publisher;

import android.graphics.Color;
import android.widget.FrameLayout;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class Plot {

    private XYPlot myPlot;
    private Timer mTimer;
    
    public Plot(FrameLayout layout) {
      // initialize our XYPlot reference:
      myPlot =  new XYPlot(layout.getContext(), "Bla");

      // Create a couple arrays of y-values to plot:
      Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
      Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

      // Turn the above arrays into XYSeries':
      XYSeries series1 = new SimpleXYSeries(
              Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
              SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
              "Series1");                             // Set the display title of the series

      // same as above
      XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

      // Create a formatter to use for drawing a series using LineAndPointRenderer:
      LineAndPointFormatter series1Format = new LineAndPointFormatter(
              Color.rgb(0, 200, 0),                   // line color
              Color.rgb(0, 100, 0),                   // point color
              null);                                  // fill color (none)

      // add a new series' to the xyplot:
      myPlot.addSeries(series1, series1Format);

      // same as above:
      myPlot.addSeries(series2,
              new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null));

      // reduce the number of range labels
      myPlot.setTicksPerRangeLabel(3);

      // by default, AndroidPlot displays developer guides to aid in laying out your plot.
      // To get rid of them call disableAllMarkup():
      myPlot.disableAllMarkup();
      
      layout.addView(myPlot);
    }

}
