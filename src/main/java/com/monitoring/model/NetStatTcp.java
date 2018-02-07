package com.monitoring.model;

public class NetStatTcp {
  private long fClose;
  private long fCloseWait;
  private long fClosing;
  private long fEstablished;
  private long fFinWait1;
  private long fFinWait2;
  private long fLastAck;
  private long fListen;
  private long fNone;
  private long fSynRecv;
  private long fSynSent;
  private long fTimeWait;

  public long getClose() {
    return fClose;
  }

  public void setClose(long aClose) {
    fClose = aClose;
  }

  public long getCloseWait() {
    return fCloseWait;
  }

  public void setCloseWait(long aCloseWait) {
    fCloseWait = aCloseWait;
  }

  public long getClosing() {
    return fClosing;
  }

  public void setClosing(long aClosing) {
    fClosing = aClosing;
  }

  public long getEstablished() {
    return fEstablished;
  }

  public void setEstablished(long aEstablished) {
    fEstablished = aEstablished;
  }

  public long getFinWait1() {
    return fFinWait1;
  }

  public void setFinWait1(long aFinWait1) {
    fFinWait1 = aFinWait1;
  }

  public long getFinWait2() {
    return fFinWait2;
  }

  public void setFinWait2(long aFinWait2) {
    fFinWait2 = aFinWait2;
  }

  public long getLastAck() {
    return fLastAck;
  }

  public void setLastAck(long aLastAck) {
    fLastAck = aLastAck;
  }

  public long getListen() {
    return fListen;
  }

  public void setListen(long aListen) {
    fListen = aListen;
  }

  public long getNone() {
    return fNone;
  }

  public void setNone(long aNone) {
    fNone = aNone;
  }

  public long getSynRecv() {
    return fSynRecv;
  }

  public void setSynRecv(long aSynRecv) {
    fSynRecv = aSynRecv;
  }

  public long getSynSent() {
    return fSynSent;
  }

  public void setSynSent(long aSynSent) {
    fSynSent = aSynSent;
  }

  public long getTimeWait() {
    return fTimeWait;
  }

  public void setTimeWait(long aTimeWait) {
    fTimeWait = aTimeWait;
  }
}
