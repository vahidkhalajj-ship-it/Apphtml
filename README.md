# BookApp - ساخت APK

## ساخت با گیت‌هاب (بدون نصب اندروید استودیو)
1. یک ریپوی Private جدید بسازید و محتوای این پوشه را آپلود کنید.
2. کلید امضا بسازید (یک‌بار، روی کامپیوتر خودتان):
   keytool -genkeypair -v -keystore release.jks -alias mykey -keyalg RSA -keysize 2048 -validity 10000
   base64 -w0 release.jks
3. در Settings > Secrets and variables > Actions این ۴ Secret را بسازید:
   KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
4. تب Actions > Build APK > Run workflow. فایل APK/AAB در Artifacts است.
فایل release.jks را جای امن نگه دارید؛ برای همه آپدیت‌ها لازم است.

## قبل از انتشار
- applicationId در app/build.gradle را به شناسه یکتای خودتان تغییر دهید.
- فونت Vazirmatn را داخل assets بگذارید تا آفلاین هم کار کند.
