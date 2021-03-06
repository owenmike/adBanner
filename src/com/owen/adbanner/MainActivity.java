package com.owen.adbanner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * ViewPager实现Banner循环滚动
 * 
 * @author owen
 */
public class MainActivity extends Activity {
	
	/** ViewPager中ImageView的容器 */
	private List<ImageView> imageViewContainer = null;
	
	
	
	/** 上一个被选中的小圆点的索引，默认值为0 */
	private int preDotPosition = 0;
	
	/** Banner文字描述数组 */
	private String[] bannerTextDescArray = { 
			"巩俐不低俗，我就不能低俗", 
			"朴树又回来了，再唱经典老歌引万人大合唱",
			"揭秘北京电影如何升级", 
			"乐视网TV版大派送", "热血屌丝的反杀" 
	};
	
	/** Banner滚动线程是否销毁的标志，默认不销毁 */
	private boolean isStop = false;
	
	/** Banner的切换下一个page的间隔时间 */
	private long scrollTimeOffset = 5000;
	
	private ViewPager viewPager;
	
	/** Banner的文字描述显示控件 */
	private TextView tvBannerTextDesc;

	/** 小圆点的父控件 */
	private LinearLayout llDotGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		startBannerScrollThread();
	}
	
	/**
	 * 开启Banner滚动线程
	 */
	private void startBannerScrollThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!isStop) {
					//每个两秒钟发一条消息到主线程，更新viewpager界面
					SystemClock.sleep(scrollTimeOffset);
					
					runOnUiThread(new Runnable() {
						public void run() {
							int newindex = viewPager.getCurrentItem() + 1;
							viewPager.setCurrentItem(newindex);
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// 销毁线程
		isStop = true;
		super.onDestroy();
	}
	
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		llDotGroup = (LinearLayout) findViewById(R.id.ll_dot_group);
		tvBannerTextDesc = (TextView) findViewById(R.id.tv_banner_text_desc);

		imageViewContainer = new ArrayList<ImageView>();
		int[] imageIDs = new int[] { 
				R.drawable.a, 
				R.drawable.b, 
				R.drawable.c,
				R.drawable.d,
				R.drawable.e, 
		};

		ImageView imageView = null;
		View dot = null;
		LayoutParams params = null;
		for (int id : imageIDs) {
			imageView = new ImageView(this);
			imageView.setBackgroundResource(id);
			imageViewContainer.add(imageView);

			// 每循环一次添加一个点到线行布局中
			dot = new View(this);
			dot.setBackgroundResource(R.drawable.dot_bg_selector);
			params = new LayoutParams(5, 5);
			params.leftMargin = 10;
			dot.setEnabled(false);
			dot.setLayoutParams(params);
			llDotGroup.addView(dot); // 向线性布局中添加"点"
		}

		viewPager.setAdapter(new BannerAdapter());
		viewPager.setOnPageChangeListener(new BannerPageChangeListener());

		// 选中第一个图片、文字描述
		tvBannerTextDesc.setText(bannerTextDescArray[0]);
		llDotGroup.getChildAt(0).setEnabled(true);
		viewPager.setCurrentItem(0);
	}

	/**
	 * ViewPager的适配器
	 */
	private class BannerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViewContainer.get(position % imageViewContainer.size()));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = imageViewContainer.get(position % imageViewContainer.size());
			
			// 为每一个page添加点击事件
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "Page 被点击了", Toast.LENGTH_SHORT).show();
				}
				
			});
			
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
	
	/**
	 * Banner的Page切换监听器
	 */
	private class BannerPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// Nothing to do
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// Nothing to do
		}

		@Override
		public void onPageSelected(int position) {
			// 取余后的索引，得到新的page的索引
			int newPositon = position % imageViewContainer.size();
			// 根据索引设置图片的描述
			tvBannerTextDesc.setText(bannerTextDescArray[newPositon]);
			// 把上一个点设置为被选中
			llDotGroup.getChildAt(preDotPosition).setEnabled(false);
			// 根据索引设置那个点被选中
			llDotGroup.getChildAt(newPositon).setEnabled(true);
			// 新索引赋值给上一个索引的位置
			preDotPosition = newPositon;
		}
		
	}
	
}
