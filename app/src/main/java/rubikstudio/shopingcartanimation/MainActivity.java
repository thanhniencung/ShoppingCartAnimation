package rubikstudio.shopingcartanimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import rubikstudio.shopingcartanimation.helper.AnimatorPath;
import rubikstudio.shopingcartanimation.helper.PathEvaluator;
import rubikstudio.shopingcartanimation.helper.PathPoint;

public class MainActivity extends AppCompatActivity {
    public static final int ANIM_DURATION = 1000;

    private ImageView imageView;
    private TextView tvNumber;

    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("U23");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.vn);

        tvNumber = findViewById(R.id.number);
        tvNumber.setText(String.valueOf(count));

        findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runAnimation();
            }
        });
    }

    private void runAnimation() {
        FrameLayout viewRoot = findViewById(R.id.rootView);
        if (viewRoot.getChildAt(1)  != null) {
            ((RelativeLayout) viewRoot.getChildAt(1)).removeAllViews();
        }

        if (viewRoot.getChildAt(1) != null) {
            viewRoot.removeViewAt(1);
        }

        FrameLayout.LayoutParams flParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        if (imageView == null) {
            imageView = new ImageView(this);
        }

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.ball);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.ball_width),
                (int) getResources().getDimension(R.dimen.ball_width));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout frameLayout = new RelativeLayout(this);
        frameLayout.addView(imageView, params);

        viewRoot.addView(frameLayout, flParams);

        int[] locationstartView = new int[2];
        imageView.getLocationInWindow(locationstartView);
        final int oldTop = locationstartView[1];

        int[] locationTargetView = new int[2];
        tvNumber.getLocationInWindow(locationTargetView);

        int left = locationTargetView[0];
        int top = viewRoot.getMeasuredHeight() - locationTargetView[1];
        int deltaY = top - oldTop;

        AnimatorPath path = new AnimatorPath();
        path.moveTo(0, 0);
        path.curveTo(0, 0, 0, -deltaY / 2, left - (int) getResources().getDimension(R.dimen.ball_right)/2, -top);

        ObjectAnimator anim = ObjectAnimator.ofObject(
                this, "buttonLoc",
                new PathEvaluator(), path.getPoints().toArray());
        anim.setDuration(ANIM_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        Animation fadeOut = new AlphaAnimation(1f, 0.1f);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
                tvNumber.setText(String.valueOf(++count));
                updateShopingCart(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOut.setDuration(ANIM_DURATION);
        imageView.startAnimation(fadeOut);
    }

    public void setButtonLoc(PathPoint newLoc) {
        imageView.setTranslationX(newLoc.mX);
        imageView.setTranslationY(newLoc.mY);
    }

    public void updateShopingCart(int numberItems) {

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(tvNumber, "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(tvNumber, "scaleY", 0.2f, 1.0f);

        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.setInterpolator(new OvershootInterpolator());
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDown.start();
    }
}
