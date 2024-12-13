import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
// Player の位置を表現するクラス
// Player １人を１オブジェクトにするとダブルスにも容易に対応できる
class Player {
  private int x,y;  // プレーヤーの位置 (プレーヤ図形の左上の座標)
  private int width,height; // プレーヤーの幅と高さ (長方形で表現)
  private int unit; // 一度の移動で移動するピクセル数
  private int bounds_y1,bounds_y2;  // プレーヤの移動範囲
                                    // とりあえず，y方向のみの移動
  public Player(int x,int y,int bounds_y1,int bounds_y2){
    this.x=x; this.y=y;
    this.bounds_y1=bounds_y1; this.bounds_y2=bounds_y2;
    width=10; height=60; unit=10;
  }
 
  // 上下の移動
  public void moveUp(){
    if (y-unit<bounds_y1) y=bounds_y1; 
    else y=y-unit;
  }
  public void moveDown(){
    if (y+unit>bounds_y2-height) y=bounds_y2-height;
    else y=y+unit;
  }
 
  // 移動 (通信先の対戦相手の位置をセットする場合に利用)
  public void setXY(int x,int y){
    this.x=x; this.y=y;
  }
   
  // ボールが当たったかチェック
  public boolean checkHit(double ball_x,double ball_y){
    if (ball_x>=x && ball_x<x+width &&
        ball_y>=y && ball_y<y+height) return true;
    return false;
  }
 
  // プレーヤーの描画
  // 四角の代わりに画像にしてもいいでしょう．
  public void draw(Graphics g){
    g.fillRect(x,y,width,height);
  }
 
  public int getX() { return x; }
  public int getY() { return y; }
  public int getWidth() { return width; }
  public int getHeight() { return height; }
}
 
// Model
// ２人プレーヤーの位置，ボールの位置，ボールの移動方向，速度
// コート大きさ  を記録
class TennisModel {
  private boolean server; // ボールの動きの計算はサーバ側で行う．
  private boolean single=false;
  private CommServer sv=null;
  private CommClient cl=null;
  public int court_size_x,court_size_y;  // テニスコートの大きさ
  public Player myself,opponent;  // Player 自分と対戦相手 
  private double ball_x,ball_y; // ballの位置 (斜めに移動するので，doubleにする)
  private double ball_radius;     // ボールの半径 
  private double ball_moving_dir; // ボールの移動方向 (0-360)
  private double ball_moving_x,ball_moving_y; // ボールの移動方向
  private double ball_speed; // ボールのスピード
 
  public TennisModel(int x,int y,int offset,boolean server,String host,int port){
    this.server=server;
    court_size_x=x; court_size_y=y;
    if (server) { // server の場合は，自分は左．clientの場合は自分は，右
      myself  =new Player(offset,y/2,0,y);
      opponent=new Player(x-offset-myself.getWidth(),y/2,0,y);
      System.out.println("Waiting for connection with port no: "+port);
      sv = new CommServer(port);
      sv.setTimeout(1); // non-wait で通信
      System.out.println("Connected !");
    }else{
      opponent=new Player(offset,y/2,0,y);
      myself  =new Player(x-offset-opponent.getWidth(),y/2,0,y);
      cl = new CommClient(host,port);
      cl.setTimeout(1); // non-wait で通信
      System.out.println("Connected to "+host+":"+port+"!");
    }
    ball_x=offset+myself.getWidth()-1; ball_y=y/2.0+myself.getHeight()/2;
    ball_moving_dir=0;
    calcMovingVector();
    ball_speed=3;
    ball_radius=10;
  }
 
  // single mode用のコンストラクタ
  public TennisModel(int x,int y,int offset){
    this.server=true;
    this.single=true;
    court_size_x=x; court_size_y=y;
    if (server) { // server の場合は，自分は左．clientの場合は自分は，右
      myself  =new Player(offset,y/2,0,y);
      opponent=new Player(x-offset-myself.getWidth(),y/2,0,y);
    }
    ball_x=offset+myself.getWidth()-1; ball_y=y/2.0+myself.getHeight()/2;
    ball_moving_dir=0;
    calcMovingVector();
    ball_speed=3;
    ball_radius=10;
  }
 
  private void calcMovingVector(){
    while (ball_moving_dir<0) ball_moving_dir+=360;
    while (ball_moving_dir>360) ball_moving_dir-=360;
    ball_moving_x=Math.cos(Math.toRadians(ball_moving_dir));
    ball_moving_y=Math.sin(Math.toRadians(ball_moving_dir));
  }
  // ボールがコート内かどうかチェック
  // 返り値: 0: コート内，1: 上方向で接触，2: 下で接触，3:左で接触，4:右で接触
  public int checkHit(double x,double y){
    if (x<=ball_radius && ball_moving_x<=0) return 3;
    if (x>=court_size_x-ball_radius && ball_moving_x>=0) return 4;
    if (y<=ball_radius && ball_moving_y<=0) return 1;
    if (y>=court_size_y-ball_radius && ball_moving_y>=0) return 2;
    return 0;
  }
 
  // ボールを1ステップ進める．
  public void moveBall(){
    if (!server) return; 
    double x0=ball_x + ball_moving_x*ball_speed;
    double y0=ball_y + ball_moving_y*ball_speed;
    int c=0,off=0; 
    if (myself.checkHit(x0,y0)) {
      if (server){
        c=5; off=myself.getX()+myself.getWidth()-1;
     }else{
        c=6; off=myself.getX();
     }
      // 打ち返す方向にランダム性を導入
      ball_moving_dir +=Math.random()*40-20;
    }else if (opponent.checkHit(x0,y0)){
      if (server){
        c=6; off=opponent.getX();
      }else{
        c=5; off=opponent.getX()+opponent.getWidth()-1;
      }
      // 打ち返す方向にランダム性を導入
      ball_moving_dir +=Math.random()*40-20;
      off=opponent.getX();
    }else {
      c=checkHit(x0,y0);
    }
    switch (c){
      case 0:
        ball_x=x0; ball_y=y0;
        return;
      case 1:  // 上に接触
      case 2:  // 下に接触
        ball_moving_dir=360-ball_moving_dir;
        calcMovingVector();
        ball_x=x0;
        break;
      case 3:  // 左に接触
      case 4:  // 右に接触
      case 5:  // プレーヤー１の左に接触
      case 6:  // プレーヤー２の右に接触
        ball_moving_dir=180-ball_moving_dir;
        calcMovingVector();
        ball_y=y0;  // 正確には，プレーヤーが打ち返した場合は，打ち返した瞬間に
                    // ランダムで方向が変化するので，それを考慮する必要があるが
                    // ここではそれは考慮せずに簡易的に実装
    }
    switch (c){
      case 1:  // 上に接触
        ball_y=2*ball_radius-y0;
        break;
      case 2:  // 下に接触
        ball_y=(court_size_y-ball_radius)*2-y0;
        break;
      case 3:  // 左に接触
        ball_x=2*ball_radius-x0;
        break;
      case 4:  // 右に接触
        ball_x=(court_size_x-ball_radius)*2.0-x0;
        break;
      case 5:  // プレーヤー１の左に接触
      case 6:  // プレーヤー２の右に接触
        ball_x=off*2.0-x0;
        break;
    }      
 
    if (c>0) // for debug
      System.out.printf("Hit %d (%.2f,%.2f) (%.2f,%.2f) %d %d %f\n", 
        c,x0,y0,ball_x,ball_y,court_size_x,off,ball_moving_dir);
  }
   
  // ボールの移動情報のセット (クライアントの場合利用)
  public void setBall(double x,double y,double dir,double speed){
    ball_x=x; ball_y=y; ball_moving_dir=dir;
    calcMovingVector();
    ball_speed=speed;
  }
 
  public void send(){
    if (server){
      String msg=String.format("%.2f %.2f %.2f %.2f %d %d",ball_x,ball_y,ball_moving_dir,ball_speed,
                        myself.getX(),myself.getY());
      sv.send(msg);
    }else{
      String msg=String.format("%d %d",myself.getX(),myself.getY());
      cl.send(msg);
    }      
  }
 
  public void recv(){
    if (server){
       String msg=sv.recv();
       if (msg==null) return; 
       String[] xy=msg.split(" ");
       opponent.setXY(Integer.parseInt(xy[0]),Integer.parseInt(xy[1]));
    }else{
       String msg=cl.recv();
       if (msg==null) return; 
       String[] xy=msg.split(" ");
       opponent.setXY(Integer.parseInt(xy[4]),Integer.parseInt(xy[5]));
       setBall(Double.parseDouble(xy[0]),Double.parseDouble(xy[1]),
          Double.parseDouble(xy[2]),Double.parseDouble(xy[3]));
    }      
  }
 
  public boolean isServer() { return server; }
  public boolean isSingle() { return single; }
   
  public void draw(Graphics g){
    g.fillOval((int)(ball_x-ball_radius),(int)(ball_y-ball_radius),
               (int)(ball_radius*2-1),(int)(ball_radius*2-1));
    myself.draw(g);
    opponent.draw(g);
    g.drawRect(0,0,court_size_x,court_size_y);
  }
}
 
// View
//   Controller はカーソルの上下だけなので，独立させないでViewの内部に実装
//
class TennisView extends JPanel implements KeyListener,ActionListener{
   private Timer timer;
   private TennisModel tm;
   private boolean ballMoving;
   final static int court_size_x=600;
   final static int court_size_y=300;
   public TennisView(TennisModel tm){
     this.tm=tm;
     ballMoving=false;
     setBackground(Color.white);
     setPreferredSize(new Dimension(tm.court_size_x+1,tm.court_size_y+1));
     setFocusable(true);
     addKeyListener(this);
     timer = new Timer(10,this); // 10ミリ秒ごとにボールが移動
     if (!tm.isServer()) timer.start();
   }
 
   public void keyPressed(KeyEvent e){
      int k = e.getKeyCode();
      if (!timer.isRunning()) timer.start(); // キーを押すとゲーム開始
      else if (k == KeyEvent.VK_DOWN) {
        tm.myself.moveDown();
        if (!tm.isServer()) tm.send();
        repaint();
      }
      else if (k == KeyEvent.VK_UP) {
        tm.myself.moveUp();
        if (!tm.isServer()) tm.send();
        repaint();
      }
   }
   public void keyReleased(KeyEvent e){ }
   public void keyTyped(KeyEvent e){ }
   
   protected void paintComponent(Graphics g){
     super.paintComponent(g);
     tm.draw(g);
   }
 
   public void actionPerformed(ActionEvent e){
     if (tm.isSingle()){
       tm.moveBall();
     }else if (tm.isServer()){
       tm.moveBall();
       tm.send();
       tm.recv();
     }else{
       tm.recv();
     }
     repaint();   
   }
 
   public static void main(String[] args){
    String str;
    boolean server=false;
    if (args.length<2){
      System.out.println("Usage : java TennisView {server/single/{host name}} {port no.} \n");     
      System.exit(1);
    }
    TennisModel tm;
    if (args[0].equals("server")){
       server=true;
       System.out.println("Server mode");
       str="server";
       tm = new TennisModel(600,300,30,server,args[0],Integer.parseInt(args[1]));
    }else if (args[0].equals("single")){
       server=true;
       System.out.println("Single mode");
       str="single";
       tm = new TennisModel(600,300,30);
    }else{
       server=false;
       System.out.println("Client mode");
       str="cilent";
       tm = new TennisModel(600,300,30,server,args[0],Integer.parseInt(args[1]));
    }
    JFrame frame  = new JFrame("TennisView ("+str+" mode)");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TennisView  tv = new TennisView(tm);
    frame.add(tv, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
  }
}