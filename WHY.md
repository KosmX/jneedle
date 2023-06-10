# Why to make another detector if there is already one?
### [MCRcortex/nekodetector](https://github.com/MCRcortex/nekodetector)

--- 

Unlike the Neko Detector, I wanted to make something more future-proof.

Comparison:

|                     | jNeedle                                                                               | Neko Detector                                                                            |
|---------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| Target              | It is a generalized tool designed to detect any JVM malware                           | Specific for Skyrage and fractureiser                                                    |
| Reliability         | Strict signature sequence matching in function, it should produce less false-positive | Custom scanning for each rule, **more reliable in detecting fractureiser specifically.** |
| Speed               | Uses an efficient KMP sequence matching algorithm with custom rules, O(n+m)           | Custom scan algorithm, slower (~2.5x slower than jNeedle with only a few rules)          |
| New virus detection | I could add a new signature in less than 25 minutes after receiving the sample        | New custom rule has to be added, way slower (maybe hours)                                |
| Database update     | Online database is updated automatically, updating jNeedle is not required.           | New program release has to be made and update is required for new detections.            |

jNeedle is a much better approach than Neko Detector, but was slower to implement. It's an improvement over the existing tool.
