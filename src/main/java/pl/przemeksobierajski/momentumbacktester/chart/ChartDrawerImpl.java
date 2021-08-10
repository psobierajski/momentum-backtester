package pl.przemeksobierajski.momentumbacktester.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;
import pl.przemeksobierajski.momentumbacktester.momentum.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
class ChartDrawerImpl implements ChartDrawer {

    @Override
    public void createChart(List<Result.NetAssetValue> results, File chartPath) throws IOException {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        results.forEach(res ->
                line_chart_dataset.addValue(res.getValue().getAmount(), "", res.getDate()));
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "title", "Date",
                "Net Asset Value",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false
        );

        int width = 2560;    /* Width of the image */
        int height = 1600;   /* Height of the image */
        ChartUtils.saveChartAsJPEG(chartPath, lineChartObject, width, height);
    }
}
