DESCRIPTION = "NVIDIA TensorRT Plugins for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = " \
  file://LICENSE;md5=a507a681eb0e43460ce60ed4ff404fad \
  file://third_party/cub/LICENSE.TXT;md5=20d1414b801e2a130d7d546685105508 \
  file://parsers/onnx/third_party/onnx/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
  file://parsers/onnx/LICENSE;md5=90c6355a9a91565fe861cb12af3c1af1 \
"

inherit cuda cmake pkgconfig

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "release/8.5"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
    file://0001-CMakeLists.txt-fix-cross-compilation-issues.patch \
    file://0002-fix-build-issues.patch \
"
# 8.5.2 tag
SRCREV = "ad932f72126f875392a4336d9ee45b2756d934a0"

S = "${WORKDIR}/git"

DEPENDS += "zlib libcublas cudnn cuda-cudart cuda-nvrtc protobuf protobuf-native tensorrt-core"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGECONFIG ??= " \
    plugin \
    parsers \
"
PACKAGECONFIG[plugin] = "-DBUILD_PLUGINS=ON,-DBUILD_PLUGINS=OFF,"
PACKAGECONFIG[parsers] = "-DBUILD_PARSERS=ON,-DBUILD_PARSERS=OFF,"

EXTRA_OECMAKE = '-DBUILD_SAMPLES=OFF -DSKIP_GPU_ARCHS=ON -DTRT_PLATFORM_ID="${TARGET_ARCH}" \
  -DCUDA_VERSION="${CUDA_VERSION}" \
  -DCUDA_INCLUDE_DIRS="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include" \
  -DENABLED_SMS="-DENABLE_SM${TEGRA_CUDA_ARCHITECTURE}" \
  -DSTABLE_DIFFUSION_GENCODES="-gencode arch=compute_${TEGRA_CUDA_ARCHITECTURE},code=compute_${TEGRA_CUDA_ARCHITECTURE}" \
'

LDFLAGS += "-Wl,--no-undefined"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPlugin.h ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPluginUtils.h ${D}${includedir}
    install -m 0644 ${S}/include/NvOnnxConfig.h ${D}${includedir}
    install -m 0644 ${S}/parsers/onnx/NvOnnxParser.h ${D}${includedir}
}
