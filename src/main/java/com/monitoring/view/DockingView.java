package com.monitoring.view;

import com.jidesoft.action.CommandBarFactory;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockableHolderPanel;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.status.LabelStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.status.TimeStatusBarItem;
import com.jidesoft.swing.JideBorderLayout;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideScrollPane;
import com.jidesoft.swing.JideTabbedPane;
import com.monitoring.model.MainSystem;
import com.monitoring.model.Node;
import com.monitoring.model.Viewer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DockingView extends JFrame implements PropertyChangeListener {
  private final ImageIcon fOnIcon = new ImageIcon(getClass().getResource("/images/green_icon.png"));
  private final ImageIcon fOffIcon = new ImageIcon(getClass().getResource("/images/red_icon.png"));
  private final SimpleDateFormat fDateFormat = new SimpleDateFormat("HH:mm:ss");
  private final LabelStatusBarItem fStatusLabel = new LabelStatusBarItem();
  private final MainSystem fMainSystem;
  private final Viewer fViewer;
  private JFrame fMainFrame = new JFrame();
  private DockableHolderPanel fHolderPanel;
  private WindowAdapter fWindowListener;

  public DockingView(MainSystem aMainSystem, Viewer aViewer) throws HeadlessException {
    if (aMainSystem == null) {
      throw new NullPointerException("Main System must not null");
    }
    fMainSystem = aMainSystem;
    fViewer = aViewer;

    fMainFrame.setTitle("Monitoring System");
    fMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fMainFrame.getContentPane().setLayout(new JideBorderLayout());
    fMainFrame.addWindowListener(fWindowListener);

    fMainFrame.setJMenuBar(createMenuBar());
  }

  public JFrame showAsFrame(final boolean exit) {
    // add a window listener so that timer can be stopped when exit
    fWindowListener = new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        clearUp();
        if (exit) {
          System.exit(0);
        }
      }
    };

    fHolderPanel = new DockableHolderPanel(fMainFrame);
    fHolderPanel.getDockingManager().addFrame(createNodeFrame());
    fHolderPanel.getDockingManager().beginLoadLayoutData();
    fHolderPanel.getDockingManager().getWorkspace().setAdjustOpacityOnFly(true);
    fHolderPanel.getDockingManager().loadLayoutDataFrom("resources");
    fHolderPanel.getDockingManager().addDockableFrameDropListener((aDockableFrame, aComponent, aI) -> false);
    fHolderPanel.getDockingManager().setOutlineMode(DockingManager.FULL_OUTLINE_MODE);
    fHolderPanel.getDockingManager().setEasyTabDock(true);
    fHolderPanel.getDockingManager().setTabbedPaneCustomizer(aJideTabbedPane -> {
      aJideTabbedPane.setTabPlacement(SwingConstants.RIGHT);
      aJideTabbedPane.setTabShape(JideTabbedPane.SHAPE_ECLIPSE3X);
    });

    fMainFrame.getContentPane().add(fHolderPanel, BorderLayout.CENTER);
    fMainFrame.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
    fMainFrame.toFront();
    fMainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    return fMainFrame;
  }

  private void clearUp() {
    fMainFrame.removeWindowListener(fWindowListener);
    fWindowListener = null;

    if (fHolderPanel.getDockingManager() != null) {
      fHolderPanel.getDockingManager().saveLayoutDataAs("resources");
    }
    fMainFrame.dispose();
    fMainFrame = null;
  }

  private DockableFrame createNodeFrame() {
    DockableFrame frame =
        new DockableFrame("Node Frame", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.FRAME1));
    frame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
    frame.getContext().setInitSide(DockContext.DOCK_SIDE_WEST);
    frame.getAutohideAction().setEnabled(true);
    frame.getCloseAction().setEnabled(false);
    frame.getFloatingAction().setEnabled(false);
    frame.getMaximizeAction().setEnabled(false);
    frame.getContext().setInitIndex(0);
    frame.setFloatable(false);
    frame.getContentPane().add(createScrollPane(addNode()));
    frame.setPreferredSize(new Dimension(200, 200));

    return frame;
  }

  private StatusBar createStatusBar() {
    StatusBar statusBar = new StatusBar();
    fStatusLabel.setText("Influxdb Server");
    fStatusLabel.setIcon(fOffIcon);
    fStatusLabel.setAlignment(JLabel.LEFT);
    statusBar.add(fStatusLabel, JideBoxLayout.VARY);

    final TimeStatusBarItem time = new TimeStatusBarItem();
    time.setTextFormat(fDateFormat);
    statusBar.add(time, JideBoxLayout.FLEXIBLE);
    final MemoryStatusBarItem gc = new MemoryStatusBarItem();
    statusBar.add(gc, JideBoxLayout.FLEXIBLE);
    return statusBar;
  }

  private JScrollPane createScrollPane(Component component) {
    return new JideScrollPane(component);
  }

  private JTree addNode() {
    DefaultMutableTreeNode resourses = new DefaultMutableTreeNode("Nodes");
    ArrayList<String> listIpAddress = new ArrayList<>();
    for (String ip : fMainSystem.getIpAddresses()) {
      Node node = fMainSystem.getByIpAddress(ip);
      resourses.add(new DefaultMutableTreeNode(node));
      listIpAddress.add(node.getHost());
      fHolderPanel.getDockingManager().addFrame(fViewer.getByIpAddress(node.getIpAddress()));
    }

    JTree fTree = new JTree(resourses);
    fTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    fTree.setCellRenderer(new TreeRenderer());

    MouseListener ml = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        JTree fTree = (JTree) e.getSource();
        TreePath selPath = fTree.getPathForLocation(e.getX(), e.getY());
        int selRow = fTree.getRowForLocation(e.getX(), e.getY());
        if (selRow != -1) {
          if (e.getClickCount() == 2) {

            for (int i = 0; i < listIpAddress.size(); i++) {
              DefaultMutableTreeNode newKey = (DefaultMutableTreeNode) selPath.getLastPathComponent();
              Node fNode = (Node) newKey.getUserObject();

              if (fNode.getHost().equalsIgnoreCase(listIpAddress.get(i))) {
                fHolderPanel.getDockingManager().showFrame(fNode.getIpAddress());
              }

            }
          }
        }
      }
    };

    fTree.addMouseListener(ml);
    return fTree;
  }

  private final class TreeRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object object = node.getUserObject();
      if (object instanceof Node) {
        Node nodeObject = (Node) object;
        return super.getTreeCellRendererComponent(tree, nodeObject.getHost(), selected, expanded, leaf,
            row, hasFocus);
      }
      return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
  }

  protected JMenuBar createMenuBar() {
    JMenuBar menu = new JMenuBar();
    JMenu lnfMenu = CommandBarFactory.createLookAndFeelMenu(fMainFrame);
    menu.add(lnfMenu);
    return menu;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    int oldValue = (int) evt.getOldValue();
    int newValue = (int) evt.getNewValue();
    if (oldValue != newValue) {
      if (newValue == 1) {
        fStatusLabel.setIcon(fOnIcon);
      } else {
        fStatusLabel.setIcon(fOffIcon);
      }
    }
  }
}
