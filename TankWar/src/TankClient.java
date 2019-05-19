
/**
 * 功能：坦克游戏的1.01
 * 1、画出坦克
 * 2、坦克动起来
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.*;

public class TankClient extends JFrame implements ActionListener{
	MyPanel mp=null;
	MyStartPanel msp=null;
	
	//make menu initial
	JMenuBar jmb=null;
	//start game
	JMenu jm1=null;
	JMenuItem jmi1=null;
	//exit game
	JMenuItem jmi2=null;
	//save game
	JMenuItem jmi3=null;
	//load game
	JMenuItem jmi4=null;
	
	public static void main(String[] args) {
		TankClient mt=new TankClient();
	}
	/**
	 * 
	 */
	public TankClient(){
		//make menu
		jmb=new JMenuBar();
		jm1=new JMenu("GAME");
		//
		jmi1=new JMenuItem("Start GAME");
		jmi2=new JMenuItem("Exit GAME");
		jmi3=new JMenuItem("Save GAME");
		jmi4=new JMenuItem("Load GAME");;
		//response
		jmi1.addActionListener(this);
		jmi1.setActionCommand("start");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("save");
		jmi4.addActionListener(this);
		jmi4.setActionCommand("load");
		//shortcut
		jm1.setMnemonic('G');
		jmi1.setMnemonic('N');
		jmi2.setMnemonic('E');
		jmi3.setMnemonic('S');
		jmi4.setMnemonic('L');
		//add
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jm1.add(jmi4);
		jmb.add(jm1);
		this.setJMenuBar(jmb);
		/*mp=new MyPanel();
		Thread ta=new Thread(mp);
		ta.start();
		this.addKeyListener(mp);
		this.add(mp);
		*/
		msp=new MyStartPanel();
		this.add(msp);
		Thread ta2=new Thread(msp);
		ta2.start();
		
		this.setTitle("TankWar");
		this.setSize(400, 400);
		setBackground(Color.RED);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
		this.setVisible(true);
	}
	public void setover() {
		this.remove(mp);
		GameOverPanel mover=null;
		mover=new GameOverPanel();
		this.add(mover);
		Thread ta2=new Thread(mover);
		ta2.start();
		this.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("start")) {
			this.remove(msp);
			mp=new MyPanel("new");
			
			
			//listen the value in mp
			mp.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setover();
				}
			});
			
			Thread ta=new Thread(mp);
			ta.start();
			this.addKeyListener(mp);
			
			this.add(mp);
			//important! visible
			this.setVisible(true);
		}else if(e.getActionCommand().equals("exit")){
			Recorder.keepRecording();
			System.exit(0);
		}else if(e.getActionCommand().equals("save")){
			Recorder.setEts(mp.ets);
			Recorder.keepRecAndEnemyTank();
			System.exit(0);
		}else if(e.getActionCommand().equals("load")){
			this.remove(msp);
			mp=new MyPanel("load");
			Thread ta=new Thread(mp);
			ta.start();
			this.addKeyListener(mp);
			this.add(mp);
			//important! visible
			this.setVisible(true);
			
		}
	}
}

//start
class MyStartPanel extends JPanel implements Runnable{
	int times=0;
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 300, 400);
		if(times%2==0){
			g.setColor(Color.yellow);
			Font myFont=new Font("Times New Roman", Font.BOLD, 30);
			g.setFont(myFont);
			g.drawString("Stage: 1", 90, 180);
		}	
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			times++;
			this.repaint();
		}
	}
}

class GameOverPanel extends JPanel implements Runnable{
	int times=0;
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 300, 400);
		if(times%2==0){
			g.setColor(Color.yellow);
			Font myFont=new Font("Times New Roman", Font.BOLD, 30);
			g.setFont(myFont);
			g.drawString("Game Over", 90, 180);
		}	
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			times++;
			this.repaint();
		}
	}
}


class MyPanel extends JPanel implements KeyListener, Runnable{
	Hero hero=null;
	Vector<EnemyTank> ets=new Vector<EnemyTank>();
	int enSize=5;
	
	//listener
	int stage=1;
	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
	       changes.addPropertyChangeListener(listener);
	    }
	 
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	       changes.removePropertyChangeListener(listener);
	    }
	
	
	//special effects when shotted
	Image bomb1=null;
	Image bomb2=null;
	Image bomb3=null;
	Vector<Bomb> bombs=new Vector<Bomb>();
	
	//nodes
	Vector<Node> nodes=new Vector<Node>();
	
	//draw initial:
	//my tank and enemy
	public MyPanel(String flag){
		//judge load or new game
		//Recorder.getRecoring();
		if (flag.equals("new")) {
			hero=new Hero(200, 270 ,1 ,1 ,1);
			//initial enemy tank
			int j=0;
			int ii=0;
			for(int i=0;i<enSize;i++){
				j=i/4;
				//System.out.println(j);
				ii=i%4;
				EnemyTank et=new EnemyTank((ii+1)*50, j*50 ,1 ,0 ,2);
				//transmit ets to the class et
				et.setEts(ets);
				Thread t1=new Thread(et);
				t1.start();
				bullet s=new bullet(et.x+10, et.y+30, 1);
				et.ss.add(s);
				Thread t2=new Thread(s);
				t2.start();
				ets.add(et);
			}
		}else if(flag.equals("load")){
			hero=new Hero(200, 270 ,1 ,1 ,1);
			nodes=new Recorder().getNodesAndEnNums();
			for(int i=0;i<nodes.size();i++){
				Node node=nodes.get(i);
				EnemyTank et=new EnemyTank(node.x, node.y, 1 ,0 ,2);
				//transmit ets to the class et
				et.setEts(ets);
				Thread t1=new Thread(et);
				t1.start();
				bullet s=new bullet(et.x+10, et.y+30, 1);
				et.ss.add(s);
				Thread t2=new Thread(s);
				t2.start();
				ets.add(et);
			}
		}
	//bomb figures
	try {
		bomb1=ImageIO.read(new File("bomb1.gif"));
		bomb2=ImageIO.read(new File("bomb2.gif"));
		bomb3=ImageIO.read(new File("bomb3.gif"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public void paint(Graphics g){
		super.paint(g);
		//record
		this.showInfo(g);
		//background
		g.fillRect(0, 0, 300, 400);
		//my tank
		if(hero.isAlive==true){
			this.drawTank(hero.getX(), hero.getY(), g, 0, this.hero.direct);
		}else {
			hero.isAlive=true;
		}
		//enemy tank
		for(int i=0;i<ets.size();i++){
			EnemyTank et=ets.get(i);
			if(et.isAlive){
				this.drawTank(ets.get(i).getX(), ets.get(i).getY() , g, 1, ets.get(i).getDirect());
				for(int j=0;j<et.ss.size();j++){
					bullet enemyShot=et.ss.get(j);
					if(enemyShot.isAlive){
						g.draw3DRect(enemyShot.x, enemyShot.y, 1, 1, false);
					}else{
						et.ss.remove(enemyShot);
					}
				}
			}
		}
		//bullet
		for(int j=0;j<this.hero.ss.size();j++) {
			bullet myBullet=hero.ss.get(j);
		if(myBullet!=null&&myBullet.isAlive==true){
			g.draw3DRect(myBullet.x, myBullet.y, 1, 1, false);
		}
		if(myBullet.isAlive==false){
			hero.ss.remove(myBullet);
		}
		}
		//bomb
		//System.out.println(bombs.size());
		for(int i=0;i<bombs.size();i++){
			Bomb b=bombs.get(i);
			if(b.life>6){
				g.drawImage(bomb1, b.x, b.y, 30, 30, this);
			}else if(b.life>3){
				g.drawImage(bomb2, b.x, b.y, 30, 30, this);
			}else {
				g.drawImage(bomb3, b.x, b.y, 30, 30, this);
			}
			b.lifeDown();
			if(b.life==0){
				bombs.remove(b);
			}
		}
		//draw record
		
		
	}
	
	public void showInfo(Graphics g){
		this.drawTank(310, 30, g, 1, 0);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman",Font.BOLD,20));
		//enemy
		g.drawString(Recorder.getEnNum()+"", 350, 50);
		
		this.drawTank(310, 70, g, 0, 0);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman",Font.BOLD,20));
		//my life
		g.drawString(Recorder.getMyLife()+"", 350, 90);
		
		//score
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman",Font.BOLD,20));
		g.drawString("Score:", 300, 130);
		
		this.drawTank(310, 140, g, 1, 0);
		g.setColor(Color.black);
		g.setFont(new Font("Times New Roman",Font.BOLD,20));
		g.drawString(Recorder.getAllEnNum()+"", 350, 160);
	}
	
	public void hitTank(bullet s,Tank et){
		switch(et.direct){
		case 0:
		case 1:
			if(s.x>et.x&&s.x<et.x+20&&s.y>et.y&&s.y<et.y+29){
				s.isAlive=false;
				et.isAlive=false;
				if(et.type==0) {
					Recorder.reduceEnNum();
					Recorder.addEnNumRec();
				}else {
					if (Recorder.getMyLife()>1) {
						Recorder.MyLifeDown();	
						hero.setX(200);
						hero.setY(270);
						hero.setDirect(1);
						//reset location
					}else {
						//game over
						Recorder.MyLifeDown();	
						System.out.println("died!");
						this.stage=-1;
						changes.firePropertyChange("str", this.stage, 0); 
					}
				}
				Bomb b=new Bomb(et.x, et.y);
				bombs.add(b);	
				System.out.println("shoot!!!");
			}
			break;
		case 2:
		case 3:
			if(s.x>et.x&&s.x<et.x+29&&s.y>et.y&&s.y<et.y+20){
				s.isAlive=false;
				et.isAlive=false;
				if(et.type==0) {
					Recorder.reduceEnNum();
					Recorder.addEnNumRec();
				}else {
					if (Recorder.getMyLife()>0) {
						Recorder.MyLifeDown();	
						hero.setX(200);
						hero.setY(270);
						hero.setDirect(1);
						//reset location
					}else {
						//game over
					}
				}
				Bomb b=new Bomb(et.x, et.y);
				bombs.add(b);	
				System.out.println("shoot!!!");
			}
			break;
		}
	}
	
	public void hitMe(){
		for(int i=0;i<this.ets.size();i++){
			EnemyTank et=ets.get(i);
			for(int j=0;j<et.ss.size();j++){
				bullet enemyShot=et.ss.get(j);	
				if(hero.isAlive) {
					this.hitTank(enemyShot, hero);
				}
			}
		}
	}
	
	public void drawTank(int x,int y,Graphics g,int type,int direct){
		switch(type){
		case 0:
			g.setColor(Color.cyan);//我的坦克颜色
			break;
		case 1:
			g.setColor(Color.yellow);//敌人坦克颜色
			break;
		case 2:
			g.setColor(Color.red);
			break;
		}
		
		switch(direct){
		case 0:
			//1、画出左边的矩形
			g.fill3DRect(x, y, 5, 30, false);
			//2、画出右边的矩形
			g.fill3DRect(x+15, y, 5, 30, false);
			//3、画出中间矩形
			g.fill3DRect(x+5, y+5, 10, 20, false);
			//4、画出中间圆形
			g.fillOval(x+5, y+10, 10, 10);
			//5、画出线(炮筒)
			g.drawLine(x+10, y+15, x+10, y);
			break;
		case 1:
			//1、画出左边的矩形
			g.fill3DRect(x, y, 5, 30, false);
			//2、画出右边的矩形
			g.fill3DRect(x+15, y, 5, 30, false);
			//3、画出中间矩形
			g.fill3DRect(x+5, y+5, 10, 20, false);
			//4、画出中间圆形
			g.fillOval(x+5, y+10, 10, 10);
			//5、画出线(炮筒)
			g.drawLine(x+10, y+15, x+10, y+29);
			break;
		case 2:
			//1、画出上边的矩形
			g.fill3DRect(x, y, 30, 5, false);
			//2、画出下边的矩形
			g.fill3DRect(x, y+15, 30, 5, false);
			//3、画出中间矩形
			g.fill3DRect(x+5, y+5, 20, 10, false);
			//4、画出中间圆形
			g.fillOval(x+10, y+5, 10, 10);
			//5、画出线(炮筒)
			g.drawLine(x+15, y+10, x, y+10);
			break;
		case 3:
			//1、画出左边的矩形
			g.fill3DRect(x, y, 30, 5, false);
			//2、画出右边的矩形
			g.fill3DRect(x, y+15, 30, 5, false);
			//3、画出中间矩形
			g.fill3DRect(x+5, y+5, 20, 10, false);
			//4、画出中间圆形
			g.fillOval(x+10, y+5, 10, 10);
			//5、画出线(炮筒)
			g.drawLine(x+15, y+10, x+29, y+10);
			break;
		}
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_W||e.getKeyCode()==KeyEvent.VK_UP){
			this.hero.setDirect(0);
			this.hero.moveUp();
		}else if(e.getKeyCode()==KeyEvent.VK_S||e.getKeyCode()==KeyEvent.VK_DOWN){
			this.hero.setDirect(1);
			this.hero.moveDown();
		}else if(e.getKeyCode()==KeyEvent.VK_A||e.getKeyCode()==KeyEvent.VK_LEFT){
			this.hero.setDirect(2);
			this.hero.moveLeft();
		}else if(e.getKeyCode()==KeyEvent.VK_D||e.getKeyCode()==KeyEvent.VK_RIGHT){
			this.hero.setDirect(3);
			this.hero.moveRight();
		}
		
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			if (this.hero.ss.size()<=4) {
				this.hero.shotEnemy();				
			}
		}
		
		//repaint
		this.repaint();
		
	}

	public void keyReleased(KeyEvent e) {
		
	}
	
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < ets.size(); i++) {
				EnemyTank et = ets.get(i);
				for (int j = 0; j < hero.ss.size(); j++) {
					bullet s = hero.ss.get(j);
					if(et.isAlive&s.isAlive){
						this.hitTank(s, et);
					}
				
				}
			}
			this.hitMe();
			this.repaint();
		}
		
	}
	
}

class Tank{
	int x=0,y=0;
	int direct=0;
	int type=1;
	int speed=4;
	boolean isAlive= true;
	
	public Tank(int x,int y){
		this.x=x;
		this.y=y;
	}

	public Tank(int x, int y, int direct, int type, int speed) {
		super();
		this.x = x;
		this.y = y;
		this.direct = direct;
		this.type = type;
		this.speed = speed;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}

}

class EnemyTank extends Tank implements Runnable{
	int move = 30;//step
	int times=0;
	Vector<EnemyTank> ets=new Vector<EnemyTank>();
	Vector<bullet> ss=new Vector<bullet>();
	
	//get enenmy tanks from panel
	public void setEts(Vector<EnemyTank> tank){
		this.ets=tank;
	}
	
	//judge overlap
	public boolean isTouchOtherEnemy(){
		boolean b=false;
		switch(this.direct){
		case 0:
			for(int i=0;i<ets.size();i++){
				EnemyTank et=ets.get(i);
				if(et!=this){
					if(et.direct==0||et.direct==1){
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30){
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y>=et.y&&this.y<=et.y+30){
							return true;
						}
					}
					if(et.direct==2||et.direct==3){
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20){
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y>=et.y&&this.y<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 1:
			for(int i=0;i<ets.size();i++){
				EnemyTank et=ets.get(i);
				if(et!=this){
					if(et.direct==0||et.direct==1){
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30){
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30){
							return true;
						}
					}
					if(et.direct==2||et.direct==3){
						if(this.x>=et.x&&this.x<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20){
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 2:
			for(int i=0;i<ets.size();i++){
				EnemyTank et=ets.get(i);
				if(et!=this){
					if(et.direct==0||et.direct==1){
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30){
							return true;
						}
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30){
							return true;
						}
					}
					if(et.direct==2||et.direct==3){
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20){
							return true;
						}
						if(this.x>=et.x&&this.x<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		case 3:
			for(int i=0;i<ets.size();i++){
				EnemyTank et=ets.get(i);
				if(et!=this){
					if(et.direct==0||et.direct==1){
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y>=et.y&&this.y<=et.y+30){
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30){
							return true;
						}
					}
					if(et.direct==2||et.direct==3){
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y>=et.y&&this.y<=et.y+20){
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20){
							return true;
						}
					}
				}
			}
			break;
		}
		return b;
	}
	
	public EnemyTank(int x,int y){
		super(x,y);
	}

	public EnemyTank(int x, int y, int direct, int type, int speed) {
		super(x, y, direct, type, speed);
	}

	@Override
	public void run() {
		while(true){
			switch(this.direct){
			case 0:
				for(int i=0;i<move;i++){
					if(y>0&&!this.isTouchOtherEnemy()){
						y-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				for(int i=0;i<move;i++){
					if(y<370&&!this.isTouchOtherEnemy()){
						y+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				for(int i=0;i<move;i++){
					if(x>0&&!this.isTouchOtherEnemy()){
						x-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				for(int i=0;i<move;i++){
					if(x<280&&!this.isTouchOtherEnemy()){
						x+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
			
			this.times++;
			if(times%2==0){
				if(this.isAlive){
					if(ss.size()<5){
						bullet s=null;
						switch(direct){
						case 0:
							s=new bullet(x+10, y, 0);
							ss.add(s);
							break;
						case 1:
							s=new bullet(x+10, y+29, 1);
							ss.add(s);
							break;
						case 2:
							s=new bullet(x, y+10, 2);
							ss.add(s);
							break;
						case 3:
							s=new bullet(x+29, y+10, 3);
							ss.add(s);
							break;
						}
						Thread t=new Thread(s);
						t.start();
					}
				}
			}

			this.direct=(int)(Math.random()*4);

			if(this.isAlive==false){
				break;
			}
		}
	}
	
}

class Hero extends Tank{
	//bullet
	Vector<bullet> ss=new Vector<bullet>();
	bullet s = null;
	
	public Hero(int x,int y){
		super(x,y);
	}
	
	public Hero(int x, int y, int direct, int type, int speed) {
		super(x, y, direct, type, speed);
	}

	public void moveUp(){
		if(y>=0) {
		y-=speed;}
	}
	public void moveRight(){
		if(x+30<=300) {
		x+=speed;}
	}
	public void moveDown(){
		if(y+20<=400) {
		y+=speed;}
	}
	public void moveLeft(){
		if(x>=0) {
		x-=speed;}
	}
	
	public void shotEnemy(){

		switch(this.direct){
		case 0:
			s=new bullet(x+10, y, 0);
			break;
		case 1:
			s=new bullet(x+10, y+29, 1);
			break;
		case 2:
			s=new bullet(x, y+10, 2);
			break;
		case 3:
			s=new bullet(x+29, y+10, 3);
			break;
		}
		ss.add(s);
		//System.out.println(s);
		Thread t=new Thread(s);
		t.start();
	}
}

//bullet
class bullet implements Runnable{
	int x,y,direct,speed=5;
	
	//is alive
	boolean isAlive= true;
	
	
	public bullet(int x, int y, int direct) {
		super();
		this.x = x;
		this.y = y;
		this.direct = direct;
	}


	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch(direct){
			case 0:
				y-=speed;
				break;
			case 1:
				y+=speed;
				break;
			case 2:
				x-=speed;
				break;
			case 3:
				x+=speed;
				break;
		}
			//System.out.println("bullet:x="+x+",y="+y);
			if(x<0||x>400||y<0||y>300){
				this.isAlive=false;
				break;
			}
		}
	}
	
}

//Bomb
class Bomb{
	int x,y;
	int life=9;
	boolean isAlive=true;
	public Bomb(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	public void lifeDown() {
		if (life>0) {
			life--;
		}else {
			this.isAlive=false;
		}
	}
}

//class recorder
class Recorder{
	//the number of enemies in current stage
	private static int enNum=20;
	//my hero life
	private static int myLife=1;
	//the number of enemies hitted
	private static int allEnNum=0;
	private static Vector<Node> nodes=new Vector<Node>();
	
	private static FileWriter fw=null;
	private static BufferedWriter bw=null;
	private static FileReader fr=null;
	private static BufferedReader br=null;
	//for recorder know the information about enemy tanks
	private static Vector<EnemyTank> ets=new Vector<EnemyTank>();
	
	//when loading, read the enemy tanks' location
	public Vector<Node> getNodesAndEnNums(){
		try {
			fr=new FileReader("e:\\tankgame\\tanksave.txt");
			br=new BufferedReader(fr);
			String n="";
			//first line: all number of enemies
			n=br.readLine();
			allEnNum=Integer.parseInt(n);		
			//alive enemy tanks
			while((n=br.readLine())!=null){
				//split according to space
				String []Recovery=n.split(" ");
				Node node=new Node(Integer.parseInt(Recovery[0]),Integer.parseInt(Recovery[1]),Integer.parseInt(Recovery[2]));
				nodes.add(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return nodes;
	}
	
	public static boolean MyLifeDown() {
		if(myLife>0) {
			myLife--;
			return true;
		}else {
			return false;
		}
		
	}

	//save enemies
	public static void keepRecAndEnemyTank(){
		try {
			fw=new FileWriter("e:\\tankgame\\tanksave.txt");
			bw=new BufferedWriter(fw);
			bw.write(allEnNum+"\r\n");
			
			for(int i=0;i<ets.size();i++){
				EnemyTank et=ets.get(i);
				if(et.isAlive){
					//save location and direction
					String recode=et.x+" "+et.y+" "+et.direct;
					bw.write(recode+"\r\n");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void getRecoring(){
		try {
			fr=new FileReader("e:\\tankgame\\tanksave.txt");
			br=new BufferedReader(fr);
			String n=br.readLine();
			allEnNum=Integer.parseInt(n);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public static void keepRecording(){
		try {
			fw=new FileWriter("e:\\tankgame\\tanksave.txt");
			bw=new BufferedWriter(fw);
			bw.write(allEnNum+"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static int getAllEnNum() {
		return allEnNum;
	}
	public static void setAllEnNum(int allEnNum) {
		Recorder.allEnNum = allEnNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}
	
	public static Vector<EnemyTank> getEts() {
		return ets;
	}

	public static void setEts(Vector<EnemyTank> ets) {
		Recorder.ets = ets;
	}

	public static void reduceEnNum(){
		enNum--;
	}
	public static void addEnNumRec(){
		allEnNum++;
	}
	
}

//record node
class Node{
	int x,y,direct;
	public Node(int x,int y,int direct){
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
}