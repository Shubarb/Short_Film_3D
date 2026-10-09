# Short Film – xem phim 3D AI

App Android (Kotlin, XML views) xem phim ngắn 3D AI. Danh sách phim (link video + metadata) được cấu hình bằng **Firebase Remote Config** dưới dạng JSON.

- Kiến trúc: **MVI + Clean Architecture**, đa module (core / feature), single-activity + Navigation Component.
- DI: Hilt · Async: Coroutines/Flow · Player: Media3 ExoPlayer (MP4, HLS `.m3u8`) · Ảnh: Coil · JSON: kotlinx.serialization.
- Build: Gradle version catalog (`gradle/libs.versions.toml`) + convention plugins (`build-logic/`).

## Mở project

1. Android Studio Ladybug (2024.2) trở lên, JDK 17+.
2. `File > Open` thư mục gốc repo, chờ Gradle sync.
3. Chạy cấu hình `app` trên máy ảo/thiết bị (minSdk 24).

`app/google-services.json` đã có sẵn cho Firebase project `short-drama-8ac4a`, `applicationId = com.short.drama.ai`.

> Package code Kotlin là `com.shortdrama.ai` (không phải `com.short.drama.ai`) vì `short` là từ khoá Java, code Java do Hilt/BuildConfig sinh ra sẽ không compile với package đó. `applicationId` vẫn là `com.short.drama.ai` để khớp Firebase.

## Cấu trúc module

```
app/                 Application (Hilt), MainActivity, màn Main (bottom navigation), nav_graph
build-logic/         Convention plugins: shortfilm.android.{application,library,feature,hilt}, shortfilm.jvm.library
core/
  model/             (Kotlin thuần) Film, Category, FilmCatalog
  domain/            (Kotlin thuần) FilmRepository (interface) + use cases
  data/              Remote Config data source, DTO + mapper, parser, FilmRepositoryImpl, Hilt modules
  common/            Dispatchers (qualifier + module), Logger
  ui/                Base MVI (MviViewModel, BaseMviFragment, BaseFragment), theme, extensions, DeepLinks
feature/
  home/              Tab Home: banner, "Big Hit", "Recommend For You", pull-to-refresh
  shorts/            Tab Short (màn trống, chờ UI)
  library/           Tab Library (màn trống, chờ UI)
  settings/          Tab Setting (màn trống, chờ UI)
  player/            Phát phim bằng ExoPlayer, fullscreen khi xoay ngang
```

Quy tắc phụ thuộc:

```
feature:* ──► core:domain ──► core:model
    │   └──► core:ui, core:common
app ──► feature:*, core:data (chỉ app biết tầng data để Hilt bind implementation)
core:data ──► core:domain, core:common
```

Feature không phụ thuộc lẫn nhau; điều hướng giữa các feature bằng deep link (`shortfilm://player/{filmId}`, xem `core/ui/.../navigation/DeepLinks.kt`).

## Màn Main

`app/.../main/MainFragment` là màn bắt đầu: mỗi tab của bottom navigation là một fragment riêng từ module feature tương ứng (Home, Short, Library, Setting). Tab được add một lần rồi show/hide nên giữ nguyên trạng thái và vị trí cuộn khi đổi tab; bấm Back ở tab khác sẽ quay về Home.

Bottom navigation nổi trên nội dung với nền kính mờ: `BlurTargetLayout` (bọc container các tab) ghi lại nội dung vào một RenderNode, `BlurBackdropLayout` vẽ lại phần nằm sau nó với `RenderEffect` blur. Blur cần Android 12+; máy cũ hơn dùng màu nền gần đặc. Nội dung cuộn của mỗi tab chừa `@dimen/sf_bottom_nav_content_inset` ở đáy để không bị thanh này che.

App vẽ tràn dưới status bar (`enableEdgeToEdge`); màn nào cần tránh status bar thì tự cộng inset (Home cộng vào header).

Font Plus Jakarta Sans (OFL) nằm trong `core/ui/src/main/res/font`, text style theo Figma là `TextAppearance.ShortFilm.*` trong `core/ui/.../themes.xml`, màu là `sf_*` trong `colors.xml`.

## MVI

Mỗi màn hình có một file `XxxContract.kt` gồm:

- `Intent` – hành động của người dùng (`HomeIntent.Refresh`, `HomeIntent.SelectCategory`, ...)
- `State` – data class bất biến mô tả toàn bộ UI
- `Effect` – sự kiện một lần (điều hướng, snackbar)

```
Fragment ──dispatch(intent)──► MviViewModel ──setState { copy(...) }──► StateFlow<State> ──► render(state)
                                    └──sendEffect(effect)──► Flow<Effect> ──► handleEffect(effect)
```

Thêm màn hình mới: tạo module `feature/xxx` với `plugins { alias(libs.plugins.shortfilm.android.feature) }`, thêm `include(":feature:xxx")` vào `settings.gradle.kts`, viết Contract + ViewModel (kế thừa `MviViewModel`) + Fragment (kế thừa `BaseMviFragment`), và `<include>` nav graph của nó trong `app/src/main/res/navigation/nav_graph.xml`.

## Remote Config

Key: **`film_catalog`** (kiểu JSON). Ví dụ:

```json
{
  "version": 1,
  "categories": [
    { "id": "trending", "title": "Trending" },
    { "id": "scifi", "title": "Sci-Fi" }
  ],
  "films": [
    {
      "id": "big-buck-bunny",
      "title": "Big Buck Bunny",
      "description": "Mô tả phim",
      "thumbnailUrl": "https://.../poster.jpg",
      "videoUrl": "https://.../video.mp4",
      "durationSec": 596,
      "categoryId": "trending",
      "tags": ["3d", "ai"],
      "isPremium": false
    }
  ]
}
```

| Trường | Bắt buộc | Ghi chú |
|---|---|---|
| `films[].id` | ✓ | Duy nhất, dùng trong deep link |
| `films[].videoUrl` | ✓ | MP4 hoặc HLS `.m3u8` |
| `films[].categoryId` | | Khớp `categories[].id` để lọc |
| `films[].thumbnailUrl` | | Ảnh ngang, dùng cho thẻ "Big Hit" |
| `films[].posterUrl` | | Ảnh dọc cho banner và thẻ "Recommend For You"; thiếu thì dùng `thumbnailUrl` |
| `films[].tags` | | Thể loại hiển thị, ví dụ `["Sci-Fi", "3D"]` |
| `films[].rating` | | Điểm 0–10, hiện trên banner |
| `films[].releasedEpisodes`, `films[].totalEpisodes` | | Hiện "12/36 Ep"; `totalEpisodes = 0` thì ẩn |
| `films[].badge` | | Nhãn góc thẻ, ví dụ `"Top 10"`; để trống thì ẩn |
| các trường khác | | Có giá trị mặc định, field lạ bị bỏ qua |

Phim thiếu `id` hoặc `videoUrl` sẽ bị bỏ qua; `id` trùng chỉ giữ bản đầu.

Tab Home chia phim thành các mục (`BuildHomeFeedUseCase`): banner là 4 phim đầu theo thứ tự trong JSON, "Big Hit" là 4 phim điểm cao nhất, "Recommend For You" là tất cả phim.

Luồng dữ liệu:

1. Lần đầu mở app (hoặc offline) hiển thị catalog mặc định đóng gói trong `core/data/src/main/res/raw/default_film_catalog.json`.
2. Home gọi `fetchAndActivate()` nền; kéo để làm mới gọi lại.
3. Lắng nghe real-time update của Remote Config: sửa `film_catalog` trên console và publish là danh sách cập nhật ngay khi app đang mở.
4. Nếu JSON trên console bị lỗi cú pháp, app log lỗi và quay về catalog mặc định thay vì hiển thị trống.

Debug build fetch không cache (`minimumFetchInterval = 0`), release cache 1 giờ (`FirebaseFilmRemoteDataSource`).

Cách publish: Firebase Console → Remote Config → *Add parameter* → key `film_catalog`, *Data type* = JSON → dán JSON → *Publish changes*.

## Test

```
./gradlew :core:domain:test :core:data:testDebugUnitTest
```
