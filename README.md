# ImageSelectLib

图片选择

## 配置

在项目的build.gradle中,添加:

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在使用库的module中添加,为避免重复引用,推荐使用exclude:
```groovy
    dependencies {
        compile "com.android.support:appcompat-v7:latest.release.version"
        compile "com.android.support:recyclerview-v7:latest.release.version"
        compile ('com.github.shucc:ImageSelectLib:v0.3') {
            exclude group: 'com.android.support', module: 'appcompat-v7'
            exclude group: 'com.android.support', module: 'recyclerview-v7'
        }
    }
```

## 使用

初始化图片加载,推荐放在Application中:
```java
    ImageLoader.init(new LocalImageLoader.LocalImageLoaderListener() {
        @Override
        public void load(Context context, PhotoView photoView, String imageUrl) {
            Glide.with(context)
                    .load(new File(imageUrl))
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(photoView);
        }
    });
```
打开图片选择界面:
```java
    LocalPhotoSelectActivity.launch(Activity activity, int maxSelectSize, int resultCode);
```
接收选择的图片列表
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 设置的resultCode) {
            List<String> images = (List<String>) data.getSerializableExtra(LocalIPhotoSelectActivity.KEY_LOCAL_IMAGE_SELECT);
        }
    }
```