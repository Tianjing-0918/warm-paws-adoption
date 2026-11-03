# GitHub + Cloudflare Pages 部署说明

这个仓库可以部署为完整的公网演示站点。Cloudflare Pages 托管 Vue 前端；
前端在无法连接 Spring Boot 后端时会自动使用本地演示数据。

## 部署参数

- 根目录：`frontend`
- 构建命令：`npm run build`
- 输出目录：`dist2`
- Node.js：20 或更高版本

## GitHub 与 Cloudflare Pages

1. 将本仓库推送到 GitHub。
2. 登录 Cloudflare Dashboard，进入 Workers & Pages。
3. 选择 Pages > Connect to Git，并授权 GitHub。
4. 选择本仓库和 `main` 分支。
5. 填写上面的根目录、构建命令和输出目录。
6. 保存并部署，首次构建完成后会得到 `*.pages.dev` 公网地址。

部署后的演示站点不需要本机开机。真实登录、申请提交和管理后台数据写入仍然需要
Spring Boot 后端和 MySQL；静态站点使用演示账号或本地演示数据。

## 本地生产构建

```bash
cd frontend
npm ci
npm run build
```

构建结果位于 `frontend/dist2`。
