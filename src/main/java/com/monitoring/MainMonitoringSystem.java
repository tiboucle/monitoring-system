package com.monitoring;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.monitoring.controller.SystemController;
import com.monitoring.model.MainSystem;
import com.monitoring.model.Node;
import com.monitoring.model.Viewer;
import com.monitoring.model.Agents;
import com.monitoring.util.Configuration;
import com.monitoring.view.DockingComponent;
import com.monitoring.view.DockingView;
import com.monitoring.view.JideLicense;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class MainMonitoringSystem {
  public static final Logger LOG = LoggerFactory.getLogger(MainMonitoringSystem.class);
  public static void main(String[] args) throws Exception {
    if( args.length != 1 ) {
      System.out.println( "Usage: <file.yml>" );
      return;
    }
    Yaml yaml = new Yaml();
    Configuration fileCOnfig = null;
    String stringError = null;
    try {
      InputStream inputFileConfig = Files.newInputStream(Paths.get(args[0]));
      fileCOnfig = yaml.loadAs(inputFileConfig, Configuration.class);
    } catch (Exception e) {
      stringError = "Something happened, please checking error >>> " + e.getMessage();
      LOG.error(stringError);
    }
    JideLicense.setArcheta();
    LookAndFeelFactory.setDefaultStyle(LookAndFeelFactory.XERTO_STYLE);
    Platform.setImplicitExit(false);
    final Configuration config = fileCOnfig;
    final String errorMessage = stringError;
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        MainSystem sys = new MainSystem();
        Viewer viewer = new Viewer();
        DockingView dockingView = new DockingView(sys, viewer);
        List<DockingComponent> dockedFrames = new ArrayList<>();
        try {
          if (config != null) {
            for (Agents agent : config.getAgents()) {
              sys.addNode(new Node(agent.getIp(), agent.getHostname()));
              DockingComponent dockedFrame = new DockingComponent(sys, agent.getIp());
              viewer.addDockingViewer(dockedFrame);
              dockedFrames.add(dockedFrame);
            }
            SystemController controller = new SystemController(dockingView, sys, config.getServerUrl(), 10000);
            dockedFrames.forEach(controller::addPropertyChangeListener);
            controller.start();
          } else {
            JOptionPane.showMessageDialog(dockingView, errorMessage);
          }
          dockingView.showAsFrame(true);
        } catch (Exception aE) {
          aE.printStackTrace();
        }
      }
    });
  }
}
