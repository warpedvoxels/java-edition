import json
import platform
import shutil
import subprocess
import tarfile
import zipfile
import os
import stat
from pathlib import Path

import requests
from tqdm import tqdm

home = str(Path.home())
hexalite_installer = home + '/.hexalite/installer/'

def apply_gradle_props():
    file = Path(home + '/.gradle/gradle.properties')
    file.parent.mkdir(exist_ok=True, parents=True)
    with open(file, 'r+') as stream:
        content = stream.read()
        prop = 'org.gradle.java.installations.paths='
        if prop not in content:
            prop = "\n" + str(hexalite_installer) + 'jdk/jdk-19'
            stream.write(prop)

def welcome_stuff():
    print("""
   __ __             ___ __     
  / // /____ _____ _/ (_) /____ 
 / _  / -_) \ / _ `/ / / __/ -_)
/_//_/\__/_\_\\_,_/_/_/\__/\__/ 
NOTE: Make sure you are running this at the root directory of our source code.
Welcome to the Hexalite installer! This script is going to download most tools you need
to compile the required development environment. There are a few things you need to install
after this script finishes:""")
    print('* Docker and Docker-Compose')
    match platform.system():
        case 'Linux':
            print('* CLang\n* OpenSSL-devel\n* mold (https://github.com/rui314/mold)')
        case 'Darwin':
            print('* CLang\n* OpenSSL-devel\n* zld (https://github.com/michaeleisel/zld)')
        case 'Windows':
            print('* MSVC')
            print('NOTE: Using WSL is recommend if you are running Windows.')
        case other:
            print('ERROR: Unsupported OS -> ' + other)
            exit(-1)


def download_all():
    file = open('urls.' + platform.system().lower() + ".json")
    data = json.load(file)
    file.close()
    zipped_ext = ".zip" if platform.system() == "Windows" else ".tar.gz"
    sh_ext = ".exe" if platform.system() == "Windows" else ".sh"
    download_file('protoc/protoc.zip', data["protoc"])
    download_file('rust/rustup' + sh_ext, data["rustup"], "--default-toolchain=nightly")
    download_file('jdk/jdk' + zipped_ext, data["jdk"])


def download_file(name, url, args=""):
    path = Path(hexalite_installer + name)
    shutil.rmtree(path.parent, ignore_errors=True)
    path.parent.mkdir(exist_ok=True, parents=True)
    res = requests.get(url, stream=True)
    total = int(res.headers.get('content-length', 0))
    with open(path, 'wb') as file, tqdm(desc=str(path), total=total, unit='i8', unit_scale=True,
                                        unit_divisor=1024) as progress:
        for data in res.iter_content(chunk_size=1024):
            size = file.write(data)
            progress.update(size)
    if name.endswith('.zip'):
        with zipfile.ZipFile(path, 'r') as zipped:
            zipped.extractall(path=path.parent)
    elif name.endswith('.tar.gz'):
        with tarfile.open(path, 'r:gz') as tar:
            tar.extractall(path=path.parent)
    elif name.endswith('.sh'):
        os.chmod(path, os.stat(path).st_mode | stat.S_IEXEC)
        subprocess.call([path, args, '-y'])
    elif name.endswith('.exe'):
        subprocess.call([path, args])


def main():
    Path(hexalite_installer).mkdir(exist_ok=True, parents=True)
    welcome_stuff()
    apply_gradle_props()
    download_all()
    if platform.system() == 'Windows':
        subprocess.call(['cargo', 'install', '-f', 'cargo-binutils', 'llvm-tools-preview'])
    else:
        path = Path(hexalite_installer + 'protoc/bin/protoc')
        os.chmod(path, os.stat(path).st_mode | stat.S_IEXEC)

if __name__ == "__main__":
    main()
