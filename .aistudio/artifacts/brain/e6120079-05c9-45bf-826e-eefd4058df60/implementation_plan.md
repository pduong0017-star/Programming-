# VibeCut Pro — Tái Cấu Trúc Giao Diện Tinh Gọn & Nâng Cấp Bàn Dựng Chuẩn CapCut

Bản nâng cấp này loại bỏ hoàn toàn sự rối mắt của bố cục cuộn dọc cũ, tách **Bàn dựng (NLE Studio)** thành không gian làm việc toàn màn hình chuyên biệt như CapCut thật, đồng thời bổ sung hệ thống chỉnh sửa từng phân đoạn (Per-Clip Editing), Khung hình chính (Keyframe), Đồ thị tốc độ (Curve Speed), Lớp phủ PIP, Nhãn dán (Stickers) và Biến đổi giọng nói AI (Voice Changer).

## User Review & Critical Decisions

> [!IMPORTANT]
> Các quyết định thiết kế và tính năng dưới đây đã được xác nhận trực tiếp từ lựa chọn của bạn để đảm bảo giao diện vừa tinh gọn vừa mạnh mẽ như CapCut Pro.

- **Quyết định 1 — Cấu trúc điều hướng 4 Tab & Bàn dựng Toàn màn hình**: Thu gọn thanh điều hướng chính xuống **4 tab tinh gọn** (*Chỉnh sửa, Mẫu AI, Xu hướng, Hồ sơ*). Khi nhấn *"Dự án mới"*, mở dự án có sẵn hoặc dùng mẫu, ứng dụng sẽ chuyển sang **Chế độ Bàn dựng Toàn màn hình (Full-Screen Studio Workspace)** và tự động ẩn thanh điều hướng đáy để tối đa hóa diện tích dựng phim.
- **Quyết định 2 — Bố cục Bàn dựng Cố định 1 Màn hình (Zero-Scroll Viewport) & Ngăn kéo Trượt (Bottom Drawer)**: Loại bỏ hoàn toàn thanh cuộn dọc gây rối mắt trong Bàn dựng. Màn hình được chia cố định theo tỷ lệ vàng của CapCut: **Thanh Top Bar mỏng (8%) → Màn hình Preview trung tâm (48%) → Thanh Playhead & Nút Keyframe/Undo (8%) → Timeline Đa Tầng tương tác (26%) → Thanh Dock Công cụ (10%)**. Khi chọn một công cụ hoặc chạm vào một Clip trên Timeline, **Ngăn điều khiển chuyên sâu sẽ trượt mượt mà từ đáy lên (Slide-up Sheet)** và có thể đóng gọn bất cứ lúc nào.
- **Quyết định 3 — Bộ tính năng Dựng phim Chuyên sâu (Pro NLE Capabilities)**:
  1. **Chỉnh sửa từng Clip (Per-Clip Inspector)**: Chạm trực tiếp vào bất kỳ phân đoạn Clip nào trên Timeline để Cắt ngắn/Kéo dài thời lượng (Trim), Chỉnh âm lượng riêng (Volume 0–200%), Nhân đôi clip (Duplicate), Đảo chiều hoặc Xóa clip.
  2. **Khung hình chính (Keyframe `◆`) & Đồ thị tốc độ (Curve Speed)**: Thêm hoặc xóa điểm neo Keyframe `◆` ngay tại vị trí Playhead để tạo chuyển động phóng/lia máy mượt mà, kết hợp 5 đường cong tốc độ chuẩn CapCut (*Montage, Anh hùng, Viên đạn, Nhảy vọt, Chớp nhoáng*).
  3. **Lớp phủ (PIP Overlay), Nhãn dán (Stickers) & Hiệu ứng Giọng nói AI (AI Voice Changer)**: Thêm kênh lớp phủ hình-trong-hình (PIP), chèn nhãn dán động lên khung hình, và chuyển đổi giọng đọc phụ đề bằng bộ lọc AI Voice (*Giọng Nữ Anime, Giọng Trầm Điện Ảnh, Robot Cyberpunk, Giọng Review Cuốn Hút*).

---

## 1. Overview & Core Concept

- **Trải nghiệm cốt lõi**: Biến VibeCut từ một màn hình biểu mẫu nhiều thẻ chồng chéo thành một **Trạm dựng video ngắn chuyên nghiệp (Mobile Non-Linear Editor)** có chiều sâu tương tác thực tế nhưng cực kỳ thoáng đãng.
- **Đối tượng sử dụng**: Nhà sáng tạo nội dung TikTok, Reels, Shorts cần quy trình dựng video nhanh bằng AI kết hợp khả năng tinh chỉnh chi tiết từng khung hình, đường cong nhịp độ và lớp phủ đa phương tiện.
- **Giá trị nâng cấp**:
  - **Không gian thoáng đãng**: Giảm 60% mật độ chữ và khung viền thừa trên màn hình chính và bàn dựng; ưu tiên hiển thị trực quan bằng biểu tượng, đồ thị và thao tác chạm trực tiếp trên Timeline.
  - **Độ sâu tính năng**: Mọi nút bấm trên bàn dựng đều tác động trực tiếp theo thời gian thực lên màn hình Preview Canvas và dữ liệu Timeline.

---

## 2. User Experience & Visual Design

### Luồng trải nghiệm người dùng (Key User Flows)
1. **Trang chủ Chỉnh sửa Tinh gọn (Streamlined Home)**:
   - Phần đầu trang hiển thị thẻ **"Dự án mới +"** nổi bật cùng dải **Công cụ AI 1 chạm** được thu gọn thành hàng ngang thanh thoát.
   - Danh sách **"Dự án gần đây"** hiển thị dưới dạng lưới thẻ trực quan kèm thông tin độ phân giải, thời lượng và menu thao tác nhanh.
2. **Bàn dựng Toàn màn hình Không cuộn dọc (Full-Screen Zero-Scroll Studio)**:
   - Có nút **Quay lại (`←`)** góc trái trên để lưu nháp và trở về Trang chủ bất cứ lúc nào.
   - **Màn hình Preview**: Tự động co giãn mượt mà theo khung hình `9:16`, `16:9` hoặc `1:1`, hiển thị trực tiếp hiệu ứng Curve Speed, nội suy Keyframe Zoom/Pan, lớp phủ PIP góc màn hình, nhãn dán Sticker và phụ đề Karaoke.
   - **Thanh điều khiển trung tâm**: Nút Play/Pause, đồng hồ Timecode chính xác đến phần mười giây (`00:04.2 / 00:15.0`), nút **Đánh dấu Keyframe (`◆+`)**, nút **Tách nhanh (`✂️`)** và nút **Toàn màn hình**.
   - **Timeline Đa Tầng**: Cho phép chọn từng Clip riêng lẻ (viền trắng sáng chuẩn CapCut khi đang chọn), hiển thị các viên kim cương đỏ **Keyframe (`◆`)** trên clip, kênh Lớp phủ PIP, kênh Nhãn dán & Phụ đề, và kênh Âm thanh đính hạt Beat vàng.
3. **Ngăn công cụ trượt thông minh (Contextual Slide-Up Tool Sheet)**:
   - Mặc định chỉ hiển thị thanh Dock biểu tượng mỏng ở đáy màn hình để nhường trọn không gian cho Preview và Timeline.
   - Khi người dùng chạm vào một mục trên Dock (*Chỉnh sửa Clip, Tốc độ & Curve, Lớp phủ & Sticker, Âm thanh & Giọng AI, Hiệu ứng & Màu, AutoCut AI*), ngăn công cụ tương ứng trượt nhẹ lên với chiều cao vừa vặn và nút đóng nhanh (`✕` / `✓`).

### Nhận diện Thị giác & Hệ thống Màu sắc (Visual Identity)
- **Phong cách thẩm mỹ**: *Pro Dark Studio Utilitarian* — nền đen tuyền chiều sâu cao giúp video và các vạch màu trên Timeline nổi bật tuyệt đối.
- **Bảng màu chủ đạo**:
  - `--background`: `#090A0F` (Obsidian Studio Black)
  - `--surface`: `#12151E` (Timeline Track Surface) & `#1A1E2B` (Slide-Up Drawer Surface)
  - `--primary`: `#00F5D4` (CapCut Electric Cyan cho nút Xuất, Playhead và trạng thái kích hoạt)
  - `--accent-keyframe`: `#FF3366` (Diamond Keyframe Red/Magenta cho điểm neo chuyển động và Curve Speed)
  - `--accent-audio`: `#10B981` (Emerald Waveform) & `#F59E0B` (Solar Beat Dots & Text Track)
- **Phông chữ & Phân cấp**: Sử dụng cỡ chữコンパクト (`11sp–13sp`) cho nhãn Timeline và Timecode số học đồng폭 (Monospace-like tabular numerals) để tránh nhảy chữ khi phát video.

---

## 3. Key Product Decisions & Trade-Offs

- **Quyết định 1: Tách Bàn dựng khỏi Bottom Navigation Bar**
  - *Giải pháp*: Quản lý trạng thái `isStudioOpen` độc lập với 4 tab chính. Khi mở Bàn dựng, ẩn thanh NavigationBar của hệ thống ứng dụng.
  - *Lý do*: Trong giao diện cũ, việc xuất hiện cùng lúc cả thanh công cụ CapCut lẫn thanh 5 tab điều hướng bên dưới khiến đáy màn hình bị chồng chéo 2 tầng thanh ngang rất chật chội.
- **Quyết định 2: Thay thế danh sách thẻ công cụ cố định bằng Ngăn kéo trượt theo ngữ cảnh (Contextual Bottom Sheet)**
  - *Giải pháp*: Chỉ mở bảng điều khiển chi tiết khi người dùng chủ động chọn công cụ trên Dock hoặc chạm vào một Clip/Track trên Timeline.
  - *Lý do*: Giúp người dùng luôn nhìn thấy trọn vẹn cả màn hình Preview lẫn Timeline trên cùng một khung nhìn mà không bao giờ phải cuộn trang.
- **Quyết định 3: Mô hình dữ liệu Clip độc lập (`TimelineClipItem`) thay vì mảng số nguyên đơn giản**
  - *Giải pháp*: Nâng cấp trạng thái Timeline từ danh sách độ dài đơn thuần sang danh sách đối tượng `TimelineClipItem` chứa đầy đủ: `id`, `label`, `durationSec`, `volume`, `speedMultiplier`, `speedCurvePreset`, `keyframes`, và `reversed`.
  - *Lý do*: Cho phép chỉnh sửa độc lập từng phân cảnh (ví dụ Clip 1 chạy tốc độ thường có Keyframe zoom cận cảnh, Clip 2 chạy hiệu ứng Curve Speed *Montage* và tắt tiếng).

---

## 4. Technical Architecture & Data Strategy

### Sơ đồ Kiến trúc & Luồng Giao diện (Architecture Diagram)

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        AppNavigation & Auth Gate                         │
│            (Google Sign-In via CredentialManager + AppCheck)             │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
         ┌───────────────────────────┴───────────────────────────┐
         ▼ (isStudioOpen == false)                               ▼ (isStudioOpen == true)
┌─────────────────────────────────────┐         ┌─────────────────────────────────────────┐
│     Main Shell (4 Streamlined Tabs) │         │   Full-Screen CapCut Studio Workspace   │
│  ┌───────────┬───────────┬────────┐ │         │  ┌───────────────────────────────────┐  │
│  │ Chỉnh sửa │  Mẫu AI   │Xu hướng│ │         │  │ Top Bar: ← | 9:16 | 1080P/4K |Xuất│  │
│  │ (Compact) │ (Gallery) │& Hồ sơ │ │         │  ├───────────────────────────────────┤  │
│  └─────┬─────┴─────┬─────┴───┬────┘ │         │  │   Interactive Preview Monitor     │  │
│        └───────────┼─────────┘      │         │  │ (Keyframes, Curve, PIP, Stickers) │  │
│                    │ Mở dự án / Mẫu │         │  ├───────────────────────────────────┤  │
│                    └────────────────┼────────►│  │ Transport Bar: Play | ◆+ | Split  │  │
│                                     │         │  ├───────────────────────────────────┤  │
│                                     │         │  │ Multi-Track Interactive Timeline  │  │
│                                     │         │  │  • Video Clips (Select/Trim/◆)    │  │
│                                     │         │  │  • PIP Overlay & Sticker Track    │  │
│                                     │         │  │  • Captions & AI Voice Track      │  │
│                                     │         │  │  • Audio Track + Yellow Beat Dots │  │
│                                     │         │  ├───────────────────────────────────┤  │
│                                     │         │  │ Slide-Up Contextual Tool Drawer   │  │
│                                     │         │  │ & 6-Icon CapCut Bottom Dock       │  │
│                                     │         │  └───────────────────────────────────┘  │
└─────────────────────────────────────┘         └─────────────────────────────────────────┘
```

### Mô hình Dữ liệu & Trạng thái Nâng cấp (`StudioEditorState`)
- **`clips: List<TimelineClip>`**: Mỗi clip lưu trữ thời lượng (`2s–10s`), âm lượng (`0f–2f`), tốc độ (`0.2x–4.0x`), mẫu đồ thị tốc độ (`CurvePreset`: *None, Montage, Hero, Bullet, JumpCut, Flash*), và danh sách mốc **Keyframes (`List<Float>`)** biểu diễn vị trí tương đối trên clip.
- **`selectedClipIndex: Int`**: Xác định clip nào trên Timeline đang được chọn để thanh công cụ tinh chỉnh đúng clip đó.
- **`pipOverlayPreset: String?`**: Lớp phủ hình-trong-hình hiển thị góc màn hình (*None, Cam Phản Ứng, Khung Neon Cyber, Đếm Ngược Góc*).
- **`activeStickers: List<String>`**: Nhãn dán động hiển thị trên khung hình (*🔥 Lửa Cháy, ⚡ Tia Chớp, 💯 Điểm 10, 🎵 Nốt Nhạc, 🚀 Xu Hướng*).
- **`aiVoiceEffect: String`**: Chế độ giả lập giọng đọc phụ đề & âm sắc AI (*Mặc định, Nữ Anime, Trầm Điện Ảnh, Robot Cyber, Review Nhanh*).
- **`activeDrawerTab: Int?`**: Điều khiển việc đóng/mở ngăn công cụ trượt từ đáy màn hình (`null` = đóng gọn ngăn kéo để xem Timeline rộng nhất).

### Ánh xạ Tương tác & Xử lý Sự kiện (Interactive Component Mapping)
1. **Chạm vào một Clip trên Timeline**: Chọn clip đó (`selectedClipIndex`), tô viền sáng trắng quanh clip và mở nhanh thanh thao tác Clip (*Tách, Cắt ngắn `-1s/+1s`, Âm lượng, Nhân đôi, Xóa*).
2. **Nhấn nút `◆+` (Keyframe) trên thanh Playhead**: Tự động gắn (hoặc gỡ nếu đã tồn tại) một viên kim cương đỏ `◆` tại vị trí hiện tại của clip đang chọn; màn hình Preview nội suy hiệu ứng thu phóng & xoay góc máy mượt mà qua các mốc `◆`.
3. **Chọn Đồ thị Tốc độ (Curve Speed)**: Hiển thị đường cong Bezier trực quan trong ngăn kéo và biến thiên vận tốc phát thực tế của màn hình Preview (nhanh vọt ở nhịp giữa, chậm sâu ở nhịp rơi).
4. **Bật Lớp phủ PIP / Nhãn dán / Giọng AI**: Kết xuất ngay khung hình phụ PIP có thể đổi góc trên màn hình Preview, hiển thị nhãn dán động nhấp nháy theo nhịp BPM và cập nhật huy hiệu Giọng đọc AI trên kênh Văn bản/Âm thanh.
