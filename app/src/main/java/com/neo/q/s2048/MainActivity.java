package com.neo.q.s2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * 精简版2048
 */
public class MainActivity extends AppCompatActivity {

    //定义方向常量
    enum Direction{
        up,down,left,right
    }

    TextView score_number;

    // 开始按钮
    Button startButton;

    // 16个块儿
    TextView[] textViews = new TextView[16];
    // 每个块儿的数字
    int[] numbers = new int[16];

    int score = 0;

    MyGestureListener myGestureListener;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score_number = (TextView) findViewById(R.id.score_number);

        textViews[0] = (TextView)findViewById(R.id.cell00);
        textViews[1] = (TextView)findViewById(R.id.cell01);
        textViews[2] = (TextView)findViewById(R.id.cell02);
        textViews[3] = (TextView)findViewById(R.id.cell03);
        textViews[4] = (TextView)findViewById(R.id.cell10);
        textViews[5] = (TextView)findViewById(R.id.cell11);
        textViews[6] = (TextView)findViewById(R.id.cell12);
        textViews[7] = (TextView)findViewById(R.id.cell13);
        textViews[8] = (TextView)findViewById(R.id.cell20);
        textViews[9] = (TextView)findViewById(R.id.cell21);
        textViews[10] = (TextView)findViewById(R.id.cell22);
        textViews[11] = (TextView)findViewById(R.id.cell23);
        textViews[12] = (TextView)findViewById(R.id.cell30);
        textViews[13] = (TextView)findViewById(R.id.cell31);
        textViews[14] = (TextView)findViewById(R.id.cell32);
        textViews[15] = (TextView)findViewById(R.id.cell33);

        startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startGame();
            }
        });
    }


    private void startGame(){

        // 分数清0
        score_number.setText("0");

        // 设置开始按钮文字
        startButton.setText(R.string.restart);

        // 初始数字,方块清0
        for(int i = 0; i< 16; i++){
            numbers[i] = 0;
            textViews[i].setText("");
        }


        //随机找两个位置,初始化游戏界面
        Random random = new Random();
        int startPos1 = random.nextInt(16);
        int startPos2 = random.nextInt(16);
        while(startPos1 == startPos2){
            startPos2 = random.nextInt(16);
        }

        textViews[startPos1].setText("2");
        textViews[startPos2].setText("2");

        numbers[startPos1] = 2;
        numbers[startPos2] = 2;

        // 添加手势监控
        myGestureListener = new MyGestureListener();
        gestureDetector = new GestureDetector(this,myGestureListener);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果没有点开始按钮的话，就还没有给gestureDetector 初始化，就出错了，所以catch一下
        try {
            return gestureDetector.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    //同时处理后台数据和前台显示
    private void coreAction(Direction direction){

        // 结束条件
        if (isFull() && (!canMerge()) ){
//            Toast.makeText(MainActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.gameover)
                    .setMessage(getString(R.string.your_score) + score)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }


        switch (direction){
            case up:
                // 先把空的地方并一下，刚刚没考虑到，，
                for (int i = 0; i < 4; i++){
                    ArrayList<Integer> notZero = new ArrayList<Integer>();  //用来临时存储不为0的项
                    for(int j = i;j <= i+12; j += 4){
                        if (numbers[j] != 0) {
                            notZero.add(numbers[j]);
                        }
                    }
                    int nZsize = notZero.size();
                    for(int j = i; j <= i+12; j += 4) {
                        if (nZsize > 0) {
                            numbers[j] = notZero.get(notZero.size() - nZsize);
                        }else{
                            numbers[j] = 0;
                        }
                        nZsize -= 1;
                    }
                }

                //正式进行处理
                for(int i = 0; i < 4; i++){
                    for(int j = i;j < 12; j += 4){
                        if((numbers[j] == numbers[j+4]) && (numbers[j] != 0) ){
                            numbers[j] += numbers[j+4];
                            score += numbers[j]; // 修改分数
                            for(int k = j + 4; k <12; k += 4){
                                numbers[k] = numbers[k+4];
                            }
                            numbers[i+12] = 0; //最后一位置0
                            break;  // 一行仅处理合并一次
                        }
                    }
                }
                break;

            case down:
                for(int i = 15; i > 11; i--){
                    ArrayList<Integer> notZero = new ArrayList<Integer>();  //用来临时存储不为0的项
                    for(int j = i; j >= i-12; j -= 4){
                        if (numbers[j] != 0) {
                            notZero.add(numbers[j]);
                        }
                    }
                    int nZsize = notZero.size();

                    for(int j = i; j >= i-12; j -= 4){
                        if (nZsize > 0) {
                            numbers[j] = notZero.get(notZero.size() - nZsize);
                        }else{
                            numbers[j] = 0;
                        }
                        nZsize -= 1;
                    }

                }

                for(int i = 15; i > 11; i--){
                    for(int j = i; j > 3; j -= 4){
                        if((numbers[j] == numbers[j-4]) && (numbers[j] != 0)){
                            numbers[j] += numbers[j-4];
                            score += numbers[j];
                            for (int k = j - 4; k > 3; k -= 4) {
                                numbers[k] = numbers[k-4];
                            }
                            numbers[i-12] = 0;
                            break;
                        }
                    }
                }
                break;

            case left:

                for(int i = 0; i <13; i += 4){
                    ArrayList<Integer> notZero = new ArrayList<Integer>();  //用来临时存储不为0的项

                    for(int j = i; j <= i + 3; j += 1){
                        if (numbers[j] != 0) {
                            notZero.add(numbers[j]);
                        }
                    }
                    int nZsize = notZero.size();
                    for(int j = i; j <= i + 3; j += 1){
                        if (nZsize > 0) {
                            numbers[j] = notZero.get(notZero.size() - nZsize);
                        }else{
                            numbers[j] = 0;
                        }
                        nZsize -= 1;
                    }
                }

                for(int i = 0; i <13; i += 4){
                    for(int j = i; j < i + 3; j += 1){
                        if((numbers[j] == numbers[j+1]) && (numbers[j] != 0)){
                            numbers[j] += numbers[j+1];
                            score += numbers[j];
                            for (int k = j + 1; k < i + 3; k++) {
                                numbers[k] = numbers[k+1];
                            }
                            numbers[i+3] = 0;
                            break;
                        }
                    }
                }
                break;
            case right:

                for(int i = 3; i <16; i += 4){
                    ArrayList<Integer> notZero = new ArrayList<Integer>();  //用来临时存储不为0的项
                    for(int j = i; j >= i - 3; j -= 1){
                        if (numbers[j] != 0) {
                            notZero.add(numbers[j]);
                        }
                    }
                    int nZsize = notZero.size();
                    for(int j = i; j >= i - 3; j -= 1){
                        if (nZsize > 0) {
                            numbers[j] = notZero.get(notZero.size() - nZsize);
                        }else{
                            numbers[j] = 0;
                        }
                        nZsize -= 1;
                    }
                }

                for(int i = 3; i <16; i += 4){
                    for(int j = i; j > i - 3; j -= 1){
                        if((numbers[j] == numbers[j-1]) && (numbers[j] != 0)){
                            numbers[j] += numbers[j-1];
                            score += numbers[j];
                            for (int k = j - 1; k > i - 3; k--) {
                                numbers[k] = numbers[k-1];
                            }
                            numbers[i-3] = 0;
                            break;
                        }
                    }
                }
                break;
        }



        //如果不是全满，就随机加一个
        if(!isFull()){
            addOne();
        }

        // 更新界面
        updateInterface();

    }

    // 判断格子是否都有不为0的数字
    private boolean isFull() {
        for(int i = 0; i < 16; i++){
            if (numbers[i] == 0) {
                return false;
            }
        }
        return true;
    }

    //判断是否可以合并
    private boolean canMerge(){

        for (int i = 0; i < 12; i++){
            if(i == 3 || i == 7 || i == 11) {
                //都是0也不能合并
                if(numbers[i] == numbers[i+4] && numbers[i] != 0){
                    return true;
                }
            }else if(i >= 12){
                if(numbers[i] == numbers[i+1] && numbers[i] != 0){
                    return true;
                }
            }else{
                if((numbers[i] == numbers[i+1] || numbers[i] == numbers[i+4]) && numbers[i] != 0){
                    return true;
                }
            }

        }
        return false;
    }

    private void addOne(){
        //随机生成一个位置 值设为2
        ArrayList<Integer> zeropos = new ArrayList<Integer>();
        // 所有值为0的 序号加入list
        for(int i = 0; i < 16; i++){
            if(numbers[i] == 0){
                zeropos.add(i);
            }
        }
        Random random = new Random();
        int m = random.nextInt(zeropos.size());
        numbers[(zeropos.get(m))] = 2;
    }

    private void updateInterface(){
        for (int i = 0; i < 16; i++){
            if(numbers[i] == 0){
                textViews[i].setText("");
            }else{
                textViews[i].setText(Integer.toString(numbers[i]));
            }
        }
        // 更新分数
        score_number.setText(Integer.toString(score));
    }



    // 实现游戏的手势监控
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        // 滑动最小距离
        private int MIN_MOVE = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            //向左滑
            if((e1.getX() - e2.getX())>MIN_MOVE){
                coreAction(Direction.left);
                return true;
            }
            //向右滑
            if((e2.getX() - e1.getX())>MIN_MOVE){
                coreAction(Direction.right);
                return true;
            }
            //向下滑
            if((e2.getY() - e1.getY())>MIN_MOVE){
                coreAction(Direction.down);
                return true;
            }
            //向上滑
            if((e1.getY() - e2.getY())>MIN_MOVE){
                coreAction(Direction.up);
                return true;
            }

            //默认返回
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

}




