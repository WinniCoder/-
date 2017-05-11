package com.whu_phone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whu_phone.R;
import com.whu_phone.model.MyCourse;
import com.whu_phone.presenter.CoursePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by wuhui on 2017/2/20.
 */

public class CourseView extends View {
    private String[] colors = {
            "#E8D70C", "#61849C", "#6CC9BB", "#FCC77B", "#7F7E7C",
            "#A8C660", "#FCC77B", "#DE9DD6", "#FFAE00", "#FA6E86"
    };
    private String[] weekChinese={
            "一","二","三","四","五","六","七"
    };
    private List<MyCourse> courses = new ArrayList<MyCourse>();
    private TextPaint paint1;
    private Paint paint2;
    private GestureDetector gestureDetector;
    private float averageWidth;
    private float averageHeight;

    public CourseView(Context context) {
        super(context);
    }

    public CourseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CourseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        courses = CoursePresenter.getAllCourses(CourseFragment.currentWeek);
        gestureDetector = new GestureDetector(listener);

        initPaint();
        loadCoursesView(canvas);
    }

    private void initPaint() {
        paint1 = new TextPaint();
        paint1.setColor(Color.WHITE);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setTextSize(30);

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
    }

    public void loadCoursesView(Canvas canvas) {
        averageWidth = getWidth() / 7;
        averageHeight = getHeight() / 13;
        for (int i = 0; i < courses.size(); i++) {
            MyCourse course = courses.get(i);
            paint2.setColor(Color.parseColor(colors[i % 10]));
            int w = course.getDay();
            int ht = course.getStartTime();
            int hb = course.getEndTime();
            RectF rect = new RectF((w - 1) * averageWidth, (ht - 1) * averageHeight, w * averageWidth, hb * averageHeight);
            canvas.drawRoundRect(rect, 20, 20, paint2);
            String content = course.getName() + "@" + course.getPlace();

            //获得自动换行后的文字
            Vector vector = getTextLinesVector(paint1, content, rect.height(), rect.width());
            content = vectorToString(vector);
            //文字自动换行
            StaticLayout layout = new StaticLayout(content, paint1, (int) rect.width(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.save();
            paint1.setTextAlign(Paint.Align.CENTER);
            //文字的位置
            canvas.translate(rect.left + rect.width() / 2, rect.top + (rect.height() - getFontHeight(paint1) * vector.size()) / 2);
            layout.draw(canvas);
            canvas.restore();
        }

    }

    private GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            for (MyCourse course : courses) {
                int w = course.getDay();
                int ht = course.getStartTime();
                int hb = course.getEndTime();
                if (x >= (w - 1) * averageWidth && x <= w * averageWidth && y >= (ht - 1) * averageHeight && y <= hb * averageHeight) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
//                    builder.setTitle("课程信息");
                    LinearLayout dialogLayout= (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.course_detail,null);
                    TextView name= (TextView) dialogLayout.findViewById(R.id.course_name);
                    TextView week= (TextView) dialogLayout.findViewById(R.id.course_week);
                    TextView day= (TextView) dialogLayout.findViewById(R.id.course_day);
                    TextView num= (TextView) dialogLayout.findViewById(R.id.course_num);
                    TextView place= (TextView) dialogLayout.findViewById(R.id.course_place);
                    TextView teacher= (TextView) dialogLayout.findViewById(R.id.course_teacher);
                    name.setText(course.getName());
                    week.setText(course.getStartWeek()+"-"+course.getEndWeek()+"周");
                    day.setText("星期"+weekChinese[course.getDay()-1]);
                    num.setText("第"+course.getStartTime()+"-"+course.getEndTime()+"节");
                    place.setText("上课地点："+course.getPlace());
                    teacher.setText("授课教师："+course.getTeacher());
                    builder.setView(dialogLayout);
                    builder.setCancelable(true);
                    builder.create().show();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 将文字拆分成每一行放到Vector里
     */
    public Vector getTextLinesVector(TextPaint paint, String content, float maxHeight,
                                     float maxWidth) {
        Vector mString = new Vector<>();
        int mRealLine = 0;// 字符串真实的行数
        char ch;
        int w = 0;
        int istart = 0;
        float mFontHeight = getFontHeight(paint);
        int mMaxLinesNum = (int) (maxHeight / mFontHeight);//显示的最大行数
        int count = content.length();
        for (int i = 0; i < count; i++) {
            ch = content.charAt(i);
            float[] widths = new float[1];
            String str = String.valueOf(ch);
            paint.getTextWidths(str, widths);
            if (ch == '\n') {
                mRealLine++;// 真实的行数加一
                mString.addElement(content.substring(istart, i));
                istart = i + 1;
                w = 0;
            } else {
                w += (int) Math.ceil(widths[0]);
                if (w > maxWidth) {
                    mRealLine++;// 真实的行数加一
                    mString.addElement(content.substring(istart, i));
                    istart = i;
                    i--;
                    w = 0;
                } else {
                    if (i == count - 1) {
                        mRealLine++;// 真实的行数加一
                        mString.addElement(content.substring(istart, count));
                    }
                }
            }
            //当真实行数大于显示的最大行数时跳出循环
            if (mRealLine == mMaxLinesNum) {
                break;
            }
        }
        return mString;
    }

    /**
     * 得到文字的高度
     */
    private float getFontHeight(TextPaint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();// 得到系统默认字体属性
        return fm.bottom - fm.top;
    }

    private String vectorToString(Vector strs) {
        StringBuffer ss = new StringBuffer();
        for (Object s : strs) {
            ss.append(s + "\n");
        }
        return ss.toString();
    }

}
