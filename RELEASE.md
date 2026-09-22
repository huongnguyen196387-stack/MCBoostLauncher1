# Release checklist

1. Extract this ZIP.
2. Replace/merge the files into the repository root, or use the ZIP as the complete source tree.
3. Push to GitHub.
4. Verify `Android CI` succeeds under Actions.
5. Create and push a version tag, for example:

```bash
git add .
git commit -m "MCBoost Launcher 1.2.0"
git push origin main
git tag v1.2.0
git push origin v1.2.0
```

The `GitHub Release APK` workflow will then create the release and attach `MCBoostLauncher-v1.2.0.apk`.
