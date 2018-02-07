package com.monitoring.view;

import com.jidesoft.utils.Lm;

public final class JideLicense {
  private JideLicense() {
  }
  public static void setVisualParadigm() {
    Lm.verifyLicense("Visual Paradigm International Ltd.", "Visual Paradigm for UML",
        "f5uKXT9Z.kM3cm.EbmYQEbwmkfQ8xK52");
  }

  public static void setArcheta() {
    Lm.verifyLicense("Archeta", "Airnets", "QK5qWMoo0uDXFSOCS:.W0V9D0pFh.Xf2");
  }

  public static void setLuciad() {
    Lm.verifyLicense("Luciad", "LuciadMap and LuciadATCPlayback", "qrgK3OLxuZGqOVrHJvwdtRqNxiME6IN");
  }
}
