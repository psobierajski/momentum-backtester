package pl.przemeksobierajski.momentumbacktester.chart;

import pl.przemeksobierajski.momentumbacktester.momentum.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ChartDrawer {

    void createChart(List<Result.NetAssetValue> results, File chartPath) throws IOException;
}
