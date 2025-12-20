package com.platform.common.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectDumpUtil {

    private final Path rootPath;
    private final String targetPackage;
    private final Path outputPath;
    private final long maxFileSize;
    private final Set<String> excludedDirs;
    private final Map<String, String> extensions;

    private ProjectDumpUtil(Builder builder) {
        rootPath = builder.rootPath;
        targetPackage = builder.targetPackage;
        outputPath = builder.outputPath;
        maxFileSize = builder.maxFileSize;
        excludedDirs = builder.excludedDirs;
        extensions = builder.extensions;
    }

    static void main(String[] args) {
        try {
            ProjectDumpUtil.builder()
                    .root(Paths.get("."))
                    .targetPackage("com.platform.common.entity")
                    .output(Paths.get("project.md"))
                    .build()
                    .dump();
        } catch (IOException e) {
            System.err.println("Error creating dump: " + e.getMessage());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public int dump() throws IOException {
        Path projectRoot = resolveProjectRoot(this.rootPath);

        String targetPathStr = targetPackage.replace(".", "/");
        List<Path> foundFiles = new ArrayList<>();

        System.out.println("🔍 Scanning: " + projectRoot);
        System.out.println("🎯 Package: " + targetPackage);

        Files.walkFileTree(projectRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (excludedDirs.contains(dir.getFileName().toString()) || dir.getFileName().toString().startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.size() > maxFileSize) return FileVisitResult.CONTINUE;

                String relativePath = projectRoot.relativize(file).toString().replace("\\", "/");

                if (relativePath.contains("/" + targetPathStr + "/") || relativePath.endsWith("/" + targetPathStr)) {
                    String ext = getExtension(file);
                    if (extensions.containsKey(ext)) {
                        foundFiles.add(file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        Collections.sort(foundFiles);

        if (foundFiles.isEmpty()) {
            System.out.println("⚠️ Files not found");
            return 0;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("# Packet dump: " + targetPackage + "\n\n");
            writer.write("- **Data:** " + LocalDateTime.now() + "\n");
            writer.write("- **The root of the project:** `" + projectRoot.toAbsolutePath() + "`\n");
            writer.write("- **Files:** " + foundFiles.size() + "\n\n");
            writer.write("---\n\n");

            for (Path file : foundFiles) {
                writeFileContent(writer, file, projectRoot);
            }
        }

        System.out.println("✅ The dump was successfully saved to: " + outputPath.toAbsolutePath());
        System.out.println("Files: " + foundFiles.size());
        return foundFiles.size();
    }

    private void writeFileContent(BufferedWriter writer, Path file, Path root) throws IOException {
        String relativePath = root.relativize(file).toString().replace("\\", "/");
        String ext = getExtension(file);
        String lang = extensions.getOrDefault(ext, "text");

        writer.write("### 📄 `" + relativePath + "`\n\n");
        writer.write("```" + lang + "\n");

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException | IOException e) {
            writer.write("// Error reading file: " + e.getMessage() + "\n");
        }

        writer.write("```\n\n---\n\n");
    }

    private static String getExtension(Path file) {
        String name = file.getFileName().toString();
        int idx = name.lastIndexOf('.');
        return idx == -1 ? "" : name.substring(idx);
    }

    private static Path resolveProjectRoot(Path start) {
        Path current = start.toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml")) || Files.exists(current.resolve("build.gradle"))) {
                return current;
            }
            current = current.getParent();
        }
        return start;
    }

    public static class Builder {
        private Path rootPath = Paths.get(".");
        private String targetPackage = "";
        private Path outputPath = Paths.get("project.md");
        private long maxFileSize = 200 * 1024; // 200 KB
        private final Set<String> excludedDirs = new HashSet<>(Set.of("target", ".git", ".idea", ".mvn", "node_modules", "build", "dist", ".gradle"));
        private final Map<String, String> extensions = new HashMap<>(Map.ofEntries(
                Map.entry(".java", "java"),
                Map.entry(".kt", "kotlin"),
                Map.entry(".xml", "xml"),
                Map.entry(".yaml", "yaml"),
                Map.entry(".yml", "yaml"),
                Map.entry(".json", "json"),
                Map.entry(".sql", "sql"),
                Map.entry(".properties", "properties"),
                Map.entry(".js", "javascript"),
                Map.entry(".ts", "typescript"),
                Map.entry(".html", "html"),
                Map.entry(".css", "css")
        ));

        public Builder root(Path rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder targetPackage(String targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public Builder output(Path outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder maxFileSize(long bytes) {
            maxFileSize = bytes;
            return this;
        }

        public Builder addExcludedDir(String dir) {
            excludedDirs.add(dir);
            return this;
        }

        public Builder addExtension(String ext, String lang) {
            extensions.put(ext, lang);
            return this;
        }

        public ProjectDumpUtil build() {
            if (targetPackage == null || targetPackage.isEmpty()) {
                throw new IllegalArgumentException("Target package must be set");
            }
            return new ProjectDumpUtil(this);
        }
    }
}