# メディア情報学プログラミング

# GitHubの基本的な使い方

## 基本的な操作

### リポジトリのクローン
リモートリポジトリをローカルにコピーします。

```bash
git clone https://github.com/kazuma-identity/media.git
```
### 変更のステージングとコミット

1. 変更をステージング

```bash
git add ファイル名
```
またはすべての変更をステージング
```bash
git add .
```
2. ステージングした変更をコミット

```bash
git commit -m "変更内容の説明"
```

3. 変更のプッシュ

ローカルの変更をリモートリポジトリに反映する

```bash
git push origin ブランチ名
```
