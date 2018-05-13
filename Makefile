# Makefile for Processing libraries
# Processing core.jar file must be in parent directory

# Copyright 2017 Justin Wong
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

.SUFFIXES: .java .class
.java.class:
#	Compile and create jar file
	mkdir -p bin; \
	javac -d bin -sourcepath src $*.java -cp ../core.jar src/*.java; \
	jar -cvf library/$(NAME).jar -C ./bin/ .;

# Set this to the name of your library
NAME = rappid
# List all source files here
CLASSES = \
	src/RasterFile.java \
	src/Preloader.java \
	src/Rappid.java

default: classes
classes: $(CLASSES:.java=.class)

# Update documentation
docs:
	rm -rf reference; mkdir -p reference; \
	javadoc -d reference  $(CLASSES);

# Compresses files into zip for publishing
zip:
	zip -r $(NAME).zip ../$(NAME) -i ../$(NAME)/src/\*
	zip -r $(NAME).zip ../$(NAME) -i ../$(NAME)/library/\*
	zip -r $(NAME).zip ../$(NAME) -i ../$(NAME)/reference/\*
#	zip -r $(NAME).zip ../$(NAME) -i ../$(NAME)/examples/\*
	zip -r $(NAME).zip ../$(NAME) -i ../$(NAME)/library.properties

# Remove binaries and jar file
clean:
	$(RM) library/$(NAME).jar
	$(RM) -r bin/*
