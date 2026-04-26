# ChordProgressionApp-Android

<p align="center">
  <img src="https://raw.githubusercontent.com/juryer/ChordProgressionApp-Android/main/chordpro.png" width="180" alt="アイコン">
</p>

PC版コード進行管理アプリX（[ChordProgressionAppX](https://github.com/juryer/ChordProgressionAppX)）をAndroidに移植したライト版です。  
横持ち固定でPC版に近いUIを実現し、外出先でもコード進行や楽曲の管理ができるよう設計しました。

---

## 概要

音楽制作の現場では「このコード進行をメモしておきたい」という場面が多くあります。  
PC版と同様のコンセプトで、スマートフォンでも使えるライト版として開発しました。

---

## 主な機能

- コード進行の登録・管理・検索・削除・並び替え
- コード選択画面（ダイアトニック・カテゴリ別・♯対応）
- 楽曲エディタ（キー設定・移調・歌詞入力）
- テキストプレビュー・クリップボードへのコピー出力
- 楽曲一覧・詳細表示
- JSONファイルによるデータ保存

---

## 動作環境

| 項目 | 内容 |
|------|------|
| OS | Android 7.0（API 24）以上 |
| 画面向き | 横持ち固定 |
| 言語 | Java |

---

## 開発環境・使用技術

| 項目 | 内容 |
|------|------|
| 言語 | Java 17 |
| IDE | Android Studio |
| ライブラリ | Gson 2.13.2 |
| バージョン管理 | GitHub |

---

## セットアップ

このリポジトリにはソースコードのみ含まれています。  
Android Studioで新規プロジェクトを作成し、以下の手順でコードを組み込んでください。

1. Android Studioで新規プロジェクトを作成（Empty Views Activity・Java・API 24）
2. `app/src/main/java/com/chordapp/` にJavaファイルをコピー
3. `app/src/main/res/layout/` にXMLファイルをコピー
4. `AndroidManifest.xml` の内容を反映
5. `build.gradle` の `dependencies` に以下を追加
   ```gradle
   implementation 'com.google.code.gson:gson:2.13.2'
   ```
6. Sync Now → エミュレーターまたは実機で実行

---

## クラス構成

| クラス名 | 役割 |
|---------|------|
| MainActivity | メインメニュー（最近の楽曲・メニュー表示） |
| RegisterActivity | コード進行登録・編集画面 |
| ManageActivity | コード進行管理（検索・並び替え・編集・削除） |
| ChordSelectActivity | コード選択画面（ダイアトニック・カテゴリ・♯） |
| SongEditorActivity | 楽曲エディタ（移調・歌詞・テキスト出力） |
| SongListActivity | 楽曲一覧 |
| SongDetailActivity | 楽曲詳細 |
| ChordProgression | コード進行モデル |
| ChordProgressionRepository | コード進行データ管理 |
| ChordTransposer | 移調ロジック |
| Song / SongSection | 楽曲・セクションモデル |
| SongRepository | 楽曲データ管理 |
| DataManager | JSON保存・読込 |

---

## PC版について

フル機能版はこちらをご覧ください。

- **安定版**: [ChordProgressionApp](https://github.com/juryer/ChordProgressionApp)
- **拡張版**: [ChordProgressionAppX](https://github.com/juryer/ChordProgressionAppX)

---

## ポートフォリオ

https://juryer.github.io/my-web-page/

---
## スクリーンショット

<img src="https://github.com/user-attachments/assets/09138a2e-756f-4625-90da-a4497d55bd55" width="50%">
<br>
<img src="https://github.com/user-attachments/assets/d5b587b9-2d96-4010-b59f-acc8e00a8d55" width="50%">



