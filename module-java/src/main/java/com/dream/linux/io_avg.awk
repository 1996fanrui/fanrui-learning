#!/bin/awk -f

# awk -f io_avg.awk -o xxx.io

BEGIN {
  line = 0
}

{
  if(NF==14) {
    if($1 == "Device:"){
      line++
    } else {
      devices[$1] = $1
      rrqm_s[$1] += $2
      wrqm_s[$1] += $3
      r_s[$1] += $4
      w_s[$1] += $5
      rKB_s[$1] += $6
      wKB_s[$1] += $7
      avgrq_sz[$1] += $8
      avgqu_sz[$1] += $9
      await[$1] += $10
      r_await[$1] += $11
      w_await[$1] += $12
      svctm[$1] += $13
      util[$1] += $14
    }
  }
}

END {
  printf "Device:         rrqm/s   wrqm/s     r/s     w/s    rkB/s    wkB/s avgrq-sz avgqu-sz   await r_await w_await  svctm   util\n"
  for(device in devices){
    printf "%s             %6.2f   %6.2f %7.2f %7.2f %8.2f %8.2f %8.2f %8.2f %7.2f %7.2f %7.2f %6.2f %6.2f\n",
    device, rrqm_s[device]/line, wrqm_s[device]/line, r_s[device]/line, w_s[device]/line,
    rKB_s[device]/line, wKB_s[device]/line, avgrq_sz[device]/line, avgqu_sz[device]/line,
    await[device]/line, r_await[device]/line, w_await[device]/line, svctm[device]/line, util[device]/line
  }
}