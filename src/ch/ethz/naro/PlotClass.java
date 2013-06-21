package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import org.ros.node.topic.Publisher;

import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;

import ch.ethz.naro.BatteryHandler.BatteryHandlerListener;
import ch.ethz.naro.DrivetrainHandler.DrivetrainHandlerListener;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepMode;

public class PlotClass implements DrivetrainHandlerListener, BatteryHandlerListener {
  
  // Plot
  private XYPlot _plot;
  private String _title;
  private String _dataLabel;
  private SimpleXYSeries[] _seriesContainer;
  private LineAndPointFormatter[] _seriesFormatter;
  private List<Number[]> _valueContainer = new ArrayList<Number[]>();
  
  // Values from ROS
  private int _dataSource;
  
  int N;
  int _maxX;
  
  // ---------------- Constructor --------------------------------
  @SuppressWarnings("deprecation")
  public PlotClass(FrameLayout layout, String dataSource, String dataLabel,int numVal) {
    
    // set datasource
    if(dataSource == "position") {
      _dataSource = 0;
    } else if(dataSource == "speed") {
      _dataSource = 1;
    } else if(dataSource == "torque") {
      _dataSource = 2;
    } else if(dataSource == "current") {
      _dataSource = 3;
    } else {
      Log.i("Plot", "I don't know what you wanna plot");
    }
    
    // Initialisation
    N = numVal;
    _title = "DefaultTitle";
    _dataLabel = dataLabel;
    _plot = new XYPlot(layout.getContext(), _title);// Init plot
    _seriesContainer = new SimpleXYSeries[N];
    _seriesFormatter = new LineAndPointFormatter[N];
    _maxX = 100;
    
    Integer[] colors = {Color.rgb(200, 0, 0), Color.rgb(0, 200, 0), Color.rgb(0, 0, 200), Color.rgb(200, 200, 0)};
    
    // create Series
    for(int i=0; i<N; i++) {
       
      Number[] data = {0};
      _valueContainer.add(data);
      // create serie
      _seriesContainer[i] = new SimpleXYSeries(
          Arrays.asList(_valueContainer.get(i)),    // SimpleXYSeries takes a List so turn our array into a List
          SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,   // Y_VALS_ONLY means use the element index as the x value
          _dataLabel+" "+Integer.toString(i));      // Set the display title of the series
      
      // format serie
      _seriesFormatter[i] = new LineAndPointFormatter(
          colors[i],
          colors[i],
          null);
      
      // add series to plot
      _plot.addSeries(_seriesContainer[i], _seriesFormatter[i]);
      
    }
    
    // fix boundaries y-axis
    _plot.setRangeBoundaries(-10, 10, BoundaryMode.FIXED);
    // fix boundaries x-axis
    _plot.setDomainBoundaries(0, _maxX, BoundaryMode.FIXED);
    
    // set x-axis label
    _plot.setDomainLabel("time");
    
    // reduce the number of range labels
    _plot.setTicksPerRangeLabel(3);

    // by default, AndroidPlot displays developer guides to aid in laying out your plot.
    // To get rid of them call disableAllMarkup():
    _plot.disableAllMarkup();
    
    // set Background-color
    _plot.getBackgroundPaint().setColor(Color.WHITE);
    _plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
    _plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
    
    _plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
    _plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
    
    // set text color
    _plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
    _plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
    
    // set grid spacing
    _plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 10);
    _plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
    
    layout.addView(_plot);
        
  }
  
  // ------------------------------------------

  // --------- Handle values ----------
  @Override
  public void handleDrivetrainEvent(DrivetrainHandler handler) {
    if(_dataSource == 1 || _dataSource == 2) {
      // get values from ROS
      double [] data = handler.dynamic[_dataSource];
      
      for(int i=0;i<N; i++) {
        _seriesContainer[i].addFirst(data[i], data[i]); // add new values at the beginning
        
        // free values at the end
        if(_seriesContainer[i].size()>_maxX) {
          _seriesContainer[i].removeLast();
        }
      }
      
      _plot.redraw();
    }
  // -------------------------------------------

  }

  @Override
  public void handleBatteryEvent(BatteryHandler handler) {
    if(_dataSource == 3) {
      // get values from ROS
      double data = handler.current;
      
      for(int i=0;i<N; i++) {
        _seriesContainer[i].addFirst(data, data); // add new values at the beginning
        
        // free values at the end
        if(_seriesContainer[i].size()>_maxX) {
          _seriesContainer[i].removeLast();
        }
      }
      
      _plot.redraw();
    }
    
  }
  
  // -------- functions for adaption ---------
  // set title
  public void setTitle(String title) {
    _title = title;
    _plot.setTitle(title);
  }
  
  // set boundaries
  public void setYBoundaries(int min, int max) {
    _plot.setRangeBoundaries(min, max, BoundaryMode.FIXED);
  }
  
  // set label y-axis
  public void setYLabel(String label) {
    _plot.setRangeLabel(label);
  }
  
  // set label x-axis
  public void setXLabel(String label) {
    _plot.setDomainLabel(label);
  }

}
